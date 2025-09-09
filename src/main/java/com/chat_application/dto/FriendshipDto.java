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
public class FriendshipDto {
    private Long id;
    private UserSummaryDto user1;
    private UserSummaryDto user2;
    private FriendStatus status;
    private UserSummaryDto requestedBy;
    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime createdAt;
}
