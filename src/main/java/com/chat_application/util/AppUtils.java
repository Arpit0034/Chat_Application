package com.chat_application.util;

import com.chat_application.entity.User;
import com.chat_application.entity.enums.UserStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

public class AppUtils {
    public static User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof User user)) {
            throw new AccessDeniedException("Invalid authenticated principal");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AccessDeniedException("User account is not active");
        }

        return user;
    }
}
