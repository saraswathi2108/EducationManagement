package com.project.student.education.repository;

import com.project.student.education.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get all unread notifications for user
    Page<Notification> findByReceiverIdAndReadFlagFalseAndDeletedFalseOrderByCreatedAtDesc(
            String receiverId, Pageable pageable);

    // Get all notifications (not deleted)
    Page<Notification> findByReceiverIdAndDeletedFalseOrderByCreatedAtDesc(
            String receiverId, Pageable pageable);

    // Count unread notifications (for bell icon)
    Long countByReceiverIdAndReadFlagFalseAndDeletedFalse(String receiverId);

    List<Notification> findByDeletedTrue();
}
