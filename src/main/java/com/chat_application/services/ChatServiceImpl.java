package com.chat_application.services;

import com.chat_application.dto.ChatCreateRequestDto;
import com.chat_application.dto.ChatDto;
import com.chat_application.dto.ChatSummaryDto;
import com.chat_application.entity.Chat;
import com.chat_application.entity.ChatParticipant;
import com.chat_application.entity.User;
import com.chat_application.entity.enums.ChatRole;
import com.chat_application.entity.enums.ChatType;
import com.chat_application.exception.ResourceNotFoundException;
import com.chat_application.repositories.ChatRepository;
import com.chat_application.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.chat_application.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService{

    private final ChatRepository chatRepository ;
    private final ModelMapper modelMapper ;
    private final UserRepository userRepository ;

    @Transactional
    @Override
    public ChatDto createChat(ChatCreateRequestDto chatCreateRequestDto) {
        log.info("Creating new chat with name: {}", chatCreateRequestDto.getName());
        Chat chat = Chat.builder()
                .name(chatCreateRequestDto.getName())
                .type(chatCreateRequestDto.getType())
                .build() ;
        Chat finalChat = chat;
        List<ChatParticipant> users = chatCreateRequestDto
                .getParticipantIds()
                .stream()
                .map(ele -> {
                    return ChatParticipant
                            .builder()
                            .joinedAt(LocalDateTime.now())
                            .chat(finalChat)
                            .chatRole(chatCreateRequestDto.getType() == ChatType.ONE_TO_ONE ? ChatRole.MEMBER : ChatRole.ADMIN)
                            .user(userRepository.findById(ele).orElseThrow(() -> new ResourceNotFoundException("User not found with id : "+ele)))
                            .build();
                })
                .collect(Collectors.toList()) ;
        chat.setParticipants(users);
        chat = chatRepository.save(chat) ;
        log.info("Chat created with id: {}", chat.getId());
        return modelMapper.map(chat,ChatDto.class) ;
    }

    @Override
    public List<ChatSummaryDto> getChatByName(String name) {
        log.info("Fetching chats by name: {}", name);
        User user = getCurrentUser() ;
        List<Chat> chats = chatRepository.findByChatName(name) ;
        List<Chat> filterChats = chats
                .stream()
                .filter(
                        chat -> chat
                                .getParticipants()
                                .stream()
                                .anyMatch(p ->p
                                        .getUser()
                                        .getId()
                                        .equals(user.getId())))
                .toList() ;
        return filterChats
                .stream()
                .map(ele -> {
                    ChatSummaryDto chatSummaryDto = modelMapper.map(ele,ChatSummaryDto.class) ;
                    chatSummaryDto.setParticipantCount(ele.getParticipants().size());
                    return chatSummaryDto ;
                })
                .toList() ;
    }

    @Override
    public Page<ChatSummaryDto> getAllChats(int page, int size) {
        log.info("Fetching all chats for current user, page: {}, size: {}", page, size);
        User currentUser = getCurrentUser();
        User user = getCurrentUser() ;
        Pageable pageable = PageRequest.of(size,page) ;
        Page<Chat> chatPage = chatRepository.findByParticipantsUserId(user) ;
        return chatPage.map(chat -> {
            ChatSummaryDto chatSummaryDto = modelMapper.map(chat,ChatSummaryDto.class);
            chatSummaryDto.setParticipantCount(chat.getParticipants().size());
            return chatSummaryDto ;
        }) ;
    }

    @Transactional
    @Override
    public void deleteChat(Long chatId) {
        User user = getCurrentUser() ;
        Chat chat =  getById(chatId) ;
        if (!checkAdmin(chat, user)) {
            throw new AccessDeniedException("Only admins can remove participants");
        }
        if(checkParticipant(chat, user.getId())){
            throw new AccessDeniedException("User with id : "+user.getId()+" not belong to chat with id :"+chatId) ;
        }

        chatRepository.delete(chat);
        log.info("Chat with id {} deleted by user {}", chatId, user.getId());
    }

    @Transactional
    @Override
    public void leaveChat(Long chatId) {
        User user = getCurrentUser() ;
        Chat chat = getById(chatId) ;
        if(checkParticipant(chat, user.getId())){
            throw new AccessDeniedException("User with id : "+user.getId()+" not belong to chat with id :"+chatId) ;
        }
        ChatParticipant chatParticipant = chat
                .getParticipants()
                .stream()
                .filter(ele -> ele
                        .getUser()
                        .getId()
                        .equals(user
                                .getId()))
                .findFirst()
                .orElseThrow();
        chat.getParticipants().remove(chatParticipant) ;
        chatRepository.save(chat) ;
        log.info("User {} left chat {}", user.getId(), chatId);
    }

    private Chat getById(Long chatId){
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + chatId));
    }

    private boolean checkAdmin(Chat chat , User user){
        return chat.getParticipants().stream()
                .anyMatch(participant -> participant.getUser().getId().equals(user.getId()) && participant.getChatRole() == ChatRole.ADMIN);
    }

    private boolean checkParticipant(Chat chat , Long userId){
        return chat.getParticipants().stream()
                .noneMatch(participant -> participant.getUser().getId().equals(userId));
    }
}
