package com.chat_application.services;

import com.chat_application.dto.NotificationCreateDto;
import com.chat_application.dto.NotificationDto;
import com.chat_application.dto.NotificationSummaryDto;

import java.util.List;

public interface NotificationService {
    NotificationDto createNotification(NotificationCreateDto notificationCreateDto);

    List<NotificationSummaryDto> getNotificationsForUser(int page, int size);

    void markAsRead(Long notificationId);

    void deleteNotification(Long notificationId);

    long getUnreadNotificationsCount();

    void deleteAllNotification() ;
}
