package com.chat_application.dto;

import com.chat_application.entity.enums.ChatType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatCreateRequestDto {
    @NotNull(message = "Chat type is required")
    private ChatType type;

    @Size(max = 100, message = "Chat name cannot exceed 100 characters")
    private String name;

    @Size(min = 1, message = "At least one participant is required")
    private List<Long> participantIds;
}