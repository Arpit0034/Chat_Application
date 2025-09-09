package com.chat_application.dto;

import com.chat_application.entity.enums.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipSummaryDto {
    private Long id;
    private UserSummaryDto friend;
    private FriendStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private Boolean isRequestSentByMe;
}
