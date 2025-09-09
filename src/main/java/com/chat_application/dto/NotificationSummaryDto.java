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
public class NotificationSummaryDto {
    private Long id;
    private NotificationType type;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
