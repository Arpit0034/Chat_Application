package com.chat_application.services;

import com.chat_application.dto.UserDto;
import com.chat_application.dto.UserSummaryDto;

import java.util.List;

public interface MessageReadService {
    public void markAsRead(Long messageId) ;
    List<UserSummaryDto> getReadsByMessage(Long messageId);
    long getUnreadCountForMessage(Long messageId);
    boolean hasUserReadMessage(Long messageId, Long userId);
    void markAllAsReadInChat(Long chatId);
}
