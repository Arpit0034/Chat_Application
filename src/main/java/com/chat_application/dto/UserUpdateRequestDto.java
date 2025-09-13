package com.chat_application.dto;

import com.chat_application.entity.enums.OnlineStatus;
import com.chat_application.entity.enums.UserStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private Long phoneNo;
}
