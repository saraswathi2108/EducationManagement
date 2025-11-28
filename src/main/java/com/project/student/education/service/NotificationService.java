package com.project.student.education.service;

import com.project.student.education.entity.Notification;
import com.project.student.education.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repo;
    private final NotificationPushService pushService;


    public Notification sendNotification(String receiverId, String title, String message, String type) {

        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .title(title)
                .message(message)
                .type(type)
                .readFlag(false)
                .deleted(false)
                .starred(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = repo.save(notification);

        pushService.sendToUser(receiverId, saved);

        return saved;
    }


    public List<Notification> getUnread(String receiverId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> notifications =
                repo.findByReceiverIdAndReadFlagFalseAndDeletedFalseOrderByCreatedAtDesc(
                        receiverId, pageable);

        return notifications.getContent();
    }


    public List<Notification> getAll(String receiverId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Notification> notifications =
                repo.findByReceiverIdAndDeletedFalseOrderByCreatedAtDesc(
                        receiverId, pageable);

        return notifications.getContent();
    }


    public void markAsRead(Long id) {
        repo.findById(id).ifPresent(n -> {
            n.setReadFlag(true);
            repo.save(n);
        });
    }


    public void delete(Long id) {
        repo.findById(id).ifPresent(n -> {
            n.setDeleted(true);
            repo.save(n);
        });
    }


    public void star(Long id) {
        repo.findById(id).ifPresent(n -> {
            n.setStarred(true);
            repo.save(n);
        });
    }

    public void unstar(Long id) {
        repo.findById(id).ifPresent(n -> {
            n.setStarred(false);
            repo.save(n);
        });
    }


    public Long getUnreadCount(String receiverId) {
        return repo.countByReceiverIdAndReadFlagFalseAndDeletedFalse(receiverId);
    }


    public List<Notification> getDeleted() {
        return repo.findByDeletedTrue();
    }
}
