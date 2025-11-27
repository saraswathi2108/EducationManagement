package com.project.student.education.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class NotificationPushService {

    // receiverId -> list of connected SSE emitters
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();


    /**
     * Subscribe user for real-time notifications via SSE
     */
    public SseEmitter subscribe(String receiverId) {

        // 0L = infinite timeout (connection stays open)
        SseEmitter emitter = new SseEmitter(0L);

        emitters.computeIfAbsent(receiverId, k ->
                Collections.synchronizedList(new ArrayList<>())
        ).add(emitter);

        log.info("SSE connected for user {}", receiverId);

        // Remove emitter on timeout / completion / network error
        emitter.onCompletion(() -> removeEmitter(receiverId, emitter));
        emitter.onTimeout(() -> removeEmitter(receiverId, emitter));
        emitter.onError((err) -> removeEmitter(receiverId, emitter));

        // Send initial connected message (optional)
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("connected"));
        } catch (IOException e) {
            removeEmitter(receiverId, emitter);
        }

        return emitter;
    }


    /**
     * Send real-time notification to a specific user
     */
    public void sendToUser(String receiverId, Object data) {

        List<SseEmitter> userEmitters = emitters.get(receiverId);

        if (userEmitters == null || userEmitters.isEmpty()) {
            log.info("User {} is offline. Notification saved only.", receiverId);
            return;
        }

        List<SseEmitter> deadEmitters = new ArrayList<>();

        userEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(data));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });

        userEmitters.removeAll(deadEmitters);
    }


    /**
     * Remove dead/closed SSE emitter
     */
    private void removeEmitter(String receiverId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(receiverId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
            if (userEmitters.isEmpty()) {
                emitters.remove(receiverId);
            }
        }
        log.info("SSE disconnected for user {}", receiverId);
    }


    /**
     * Check if user is currently online
     */
    public boolean isOnline(String receiverId) {
        return emitters.containsKey(receiverId) && !emitters.get(receiverId).isEmpty();
    }


    /**
     * Allow user to manually disconnect
     */
    public String unSubscribe(String receiverId) {
        List<SseEmitter> list = emitters.remove(receiverId);
        if (list != null) list.forEach(SseEmitter::complete);
        return "Unsubscribed " + receiverId;
    }
}
