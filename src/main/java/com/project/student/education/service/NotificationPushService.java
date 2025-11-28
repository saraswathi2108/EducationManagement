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

    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();



    public SseEmitter subscribe(String receiverId) {

        SseEmitter emitter = new SseEmitter(0L);

        emitters.computeIfAbsent(receiverId, k ->
                Collections.synchronizedList(new ArrayList<>())
        ).add(emitter);

        log.info("SSE connected for user {}", receiverId);

        emitter.onCompletion(() -> removeEmitter(receiverId, emitter));
        emitter.onTimeout(() -> removeEmitter(receiverId, emitter));
        emitter.onError((err) -> removeEmitter(receiverId, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("connected"));
        } catch (IOException e) {
            removeEmitter(receiverId, emitter);
        }

        return emitter;
    }



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



    public boolean isOnline(String receiverId) {
        return emitters.containsKey(receiverId) && !emitters.get(receiverId).isEmpty();
    }



    public String unSubscribe(String receiverId) {
        List<SseEmitter> list = emitters.remove(receiverId);
        if (list != null) list.forEach(SseEmitter::complete);
        return "Unsubscribed " + receiverId;
    }
}
