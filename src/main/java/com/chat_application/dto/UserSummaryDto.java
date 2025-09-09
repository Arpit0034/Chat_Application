package com.chat_application.dto;

import com.chat_application.entity.enums.OnlineStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDto {
    private String phoneNo;
    private String name;
    private OnlineStatus onlineStatus;
    private LocalDateTime lastSeen;
}
