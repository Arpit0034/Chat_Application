package com.chat_application.dto;

import com.chat_application.entity.enums.MessageSendStatus;
import com.chat_application.entity.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private Long id;
    private Long chatId;
    private UserSummaryDto sender;
    private MessageType messageType;
    private String content;
    private MessageSendStatus sendStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AttachmentDto> attachments;
    private List<MessageReadDto> messageReads;
}

