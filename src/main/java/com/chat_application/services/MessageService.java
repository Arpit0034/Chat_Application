package com.chat_application.services;

import com.chat_application.dto.*;

import java.util.List;

public interface MessageService {
    public MessageSummaryDto createMessage(MessageCreateRequestDto messageCreateRequestDto) ;
    PagedResponseDto<MessageSummaryDto> getChatMessages(Long chatId, int page, int size);
    public void deleteMessageForMe(Long messageId) ;
    public void deleteMessageForEveryone(Long messageId) ;
    void deleteAllMessages(Long chatId);
    void markMessageAsDelivered(Long messageId) ;
}
