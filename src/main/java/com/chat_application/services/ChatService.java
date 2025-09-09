package com.chat_application.services;

import com.chat_application.dto.ChatCreateRequestDto;
import com.chat_application.dto.ChatDto;
import com.chat_application.dto.ChatParticipantDto;
import com.chat_application.dto.ChatSummaryDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ChatService {
    ChatDto createChat(ChatCreateRequestDto chatCreateRequestDto) ;
    List<ChatSummaryDto> getChatByName(String name) ;
    Page<ChatSummaryDto> getAllChats(int page,int size) ;
    void deleteChat(Long chatId);
    void leaveChat(Long chatId);
}
