package com.chat_application.dto;

import com.chat_application.entity.enums.ChatType;
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
public class ChatDto {
    private Long id;
    private ChatType chatType;
    private String name;
    private LocalDateTime createdAt;
    private List<ChatParticipantDto> participants;
}
