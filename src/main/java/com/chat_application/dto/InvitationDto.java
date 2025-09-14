package com.chat_application.dto;

import com.chat_application.entity.enums.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationDto {
    private Long id;
    private Long chatId;
    private String chatName;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private InvitationStatus invitationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}
