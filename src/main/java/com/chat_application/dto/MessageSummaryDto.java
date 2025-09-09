package com.chat_application.dto;

import com.chat_application.entity.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageSummaryDto {
    private Long id;
    private Long chatId ;
    private UserSummaryDto sender;
    private MessageType messageType;
    private String content;
    private LocalDateTime createdAt;
    private Boolean hasAttachments;
}
