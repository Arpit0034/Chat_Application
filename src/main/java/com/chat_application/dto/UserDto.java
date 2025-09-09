package com.chat_application.dto;

import com.chat_application.entity.enums.OnlineStatus;
import com.chat_application.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Long phoneNo;
    private OnlineStatus onlineStatus;
    private LocalDateTime lastSeen;
    private UserStatus status;
    private LocalDateTime createdAt;
}
