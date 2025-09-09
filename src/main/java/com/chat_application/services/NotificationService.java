package com.chat_application.services;

import com.chat_application.dto.NotificationDto;
import com.chat_application.dto.NotificationSummaryDto;
import com.chat_application.entity.enums.NotificationType;

import java.util.List;

public interface NotificationService {
    NotificationDto createNotification(Long chatId, Long messageId, NotificationType type);

    List<NotificationSummaryDto> getNotificationsForUser(int page, int size);

    void markAsRead(Long notificationId);

    void deleteNotification(Long notificationId);

    long getUnreadNotificationsCount();

    void deleteAllNotification() ;
}
