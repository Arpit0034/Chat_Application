package com.chat_application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReadDto {
    private Long id;
    private Long messageId;
    private UserSummaryDto user;
    private LocalDateTime readAt;
}

