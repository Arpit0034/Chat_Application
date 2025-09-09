package com.chat_application.dto;

import com.chat_application.entity.enums.MessageType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MessageCreateRequestDto {
    @NotNull(message = "Chat ID is required")
    private Long chatId;

    @NotNull(message = "Message type is required")
    private MessageType messageType;

    @Size(max = 10000, message = "Message content cannot exceed 1000 characters")
    private String content;

    private List<AttachmentCreateRequestDto> attachments;
}
