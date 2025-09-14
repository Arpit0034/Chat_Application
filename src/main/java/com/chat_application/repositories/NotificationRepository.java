package com.chat_application.repositories;

import com.chat_application.entity.Notification;
import com.chat_application.entity.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    Page<Notification> findByUserId(Long userId, Pageable pageable);

    long countByUserIdAndIsReadFalse(Long id);

    void deleteAllByUserId(Long id);

    Notification findTopByTypeAndMessageIdOrderByCreatedAtDesc(NotificationType notificationType, Long messageId);
}
