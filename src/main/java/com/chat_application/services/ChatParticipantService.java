package com.chat_application.services;

import com.chat_application.dto.ChatParticipantDto;

import java.util.List;

public interface ChatParticipantService {
    void addParticipant(Long chatId, Long userId);
    void removeParticipant(Long chatId, Long userId);
    public List<ChatParticipantDto> getAllParticipants(Long chatId);
    boolean isUserParticipant(Long chatId , Long userId) ;
    boolean isUserAdmin(Long chatId , Long userId) ;
    void updateParticipantRole(Long chatId , Long userId) ;
}
