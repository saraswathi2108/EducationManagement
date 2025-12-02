package com.project.student.education.controller;

import com.project.student.education.entity.Notification;
import com.project.student.education.service.NotificationPushService;
import com.project.student.education.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/student/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationPushService pushService;



    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody Notification req) {
        notificationService.sendNotification(
                req.getReceiverId(),
                req.getTitle(),
                req.getMessage(),
                req.getType()
        );
        return ResponseEntity.ok("Notification sent");
    }



    @GetMapping(
            value = "/subscribe/{receiverId}",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter subscribe(@PathVariable String receiverId) {
        log.info("User subscribed for SSE: {}", receiverId);
        return pushService.subscribe(receiverId);
    }



    @GetMapping("/unread/{receiverId}")
    public ResponseEntity<List<Notification>> getUnread(
            @PathVariable String receiverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(notificationService.getUnread(receiverId, page, size));
    }


    // ---------------------------------------------
    // 4. GET ALL NOTIFICATIONS
    // ---------------------------------------------
    @GetMapping("/all/{receiverId}")
    public ResponseEntity<List<Notification>> getAll(
            @PathVariable String receiverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(notificationService.getAll(receiverId, page, size));
    }


    // ---------------------------------------------
    // 5. MARK AS READ
    // ---------------------------------------------
    @PostMapping("/read/{id}")
    public ResponseEntity<String> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Marked as read");
    }



    @PostMapping("/star/{id}")
    public ResponseEntity<String> star(@PathVariable Long id) {
        notificationService.star(id);
        return ResponseEntity.ok("Starred");
    }

    @PostMapping("/unstar/{id}")
    public ResponseEntity<String> unstar(@PathVariable Long id) {
        notificationService.unstar(id);
        return ResponseEntity.ok("Unstarred");
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ResponseEntity.ok("Deleted");
    }



    @GetMapping("/unread-count/{receiverId}")
    public ResponseEntity<Long> unreadCount(@PathVariable String receiverId) {
        return ResponseEntity.ok(notificationService.getUnreadCount(receiverId));
    }



    @GetMapping("/isOnline/{receiverId}")
    public boolean isOnline(@PathVariable String receiverId) {
        return pushService.isOnline(receiverId);
    }



    @GetMapping("/unsubscribe/{receiverId}")
    public String unsubscribe(@PathVariable String receiverId) {
        return pushService.unSubscribe(receiverId);
    }

    @PostMapping("/read-all/{receiverId}")
    public ResponseEntity<String> markAllRead(@PathVariable String receiverId) {
        notificationService.markAllRead(receiverId);
        return ResponseEntity.ok("All marked as read");
    }

}

