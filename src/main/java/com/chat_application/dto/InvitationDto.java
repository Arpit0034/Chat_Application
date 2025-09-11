package com.chat_application.dto;

import com.chat_application.entity.Chat;
import com.chat_application.entity.User;
import com.chat_application.entity.enums.InvitationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDto {
    private Long id;
    private Chat chat;
    private User sender;
    private User receiver;
    private InvitationStatus invitationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}
