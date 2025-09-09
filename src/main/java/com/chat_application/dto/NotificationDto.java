package com.chat_application.dto;

import com.chat_application.entity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private Long userId;
    private ChatSummaryDto chat;
    private MessageSummaryDto message;
    private NotificationType type;
    private Boolean isRead;
    private LocalDateTime createdAt;
}

