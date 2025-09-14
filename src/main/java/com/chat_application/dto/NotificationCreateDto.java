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
public class NotificationCreateDto {

    private Long userId;

    private Long chatId;

    private Long messageId;

    private NotificationType type;

    private Boolean isRead;

    private String content;

    private LocalDateTime createdAt;

}
