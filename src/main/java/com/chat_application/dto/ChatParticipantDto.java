package com.chat_application.dto;

import com.chat_application.entity.enums.ChatRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatParticipantDto {
    private Long id;
    private UserSummaryDto user;
    private ChatRole chatRole;
    private LocalDateTime joinedAt;
    private LocalDateTime leftAt;
}
