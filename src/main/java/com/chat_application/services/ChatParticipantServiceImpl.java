package com.chat_application.services;

import com.chat_application.dto.ChatParticipantDto;
import com.chat_application.entity.Chat;
import com.chat_application.entity.ChatParticipant;
import com.chat_application.entity.User;
import com.chat_application.entity.enums.ChatRole;
import com.chat_application.entity.enums.ChatType;
import com.chat_application.entity.enums.FriendStatus;
import com.chat_application.exception.ResourceNotFoundException;
import com.chat_application.exception.UnAuthorisedException;
import com.chat_application.repositories.ChatParticipantRepository;
import com.chat_application.repositories.ChatRepository;
import com.chat_application.repositories.FriendshipRepository;
import com.chat_application.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.chat_application.util.AppUtils.getCurrentUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatParticipantServiceImpl implements ChatParticipantService {

    private final ChatRepository chatRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final FriendshipRepository friendshipRepository ;

    @Transactional
    @Override
    public void addParticipant(Long chatId, Long userId) {
        User currentUser = getCurrentUser();
        log.info("User {} adding participant {} to chat {}", currentUser.getId(), userId, chatId);

        Chat chat = getById(chatId);

        if (!checkAdmin(chat, currentUser)) {
            throw new AccessDeniedException("Only admins can add participants");
        }

        if (chat.getType() == ChatType.ONE_TO_ONE) {
            throw new UnAuthorisedException("Can't add participants to a ONE_TO_ONE chat");
        }

        User userToAdd = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (checkParticipant(chatId, userToAdd.getId())) {
            throw new IllegalStateException("User with id: " + userToAdd.getId() + " is already a participant in chat " + chatId);
        }

        boolean areFriends = friendshipRepository.findByUser1AndUser2(currentUser, userToAdd)
                .or(() -> friendshipRepository.findByUser1AndUser2(userToAdd, currentUser))
                .map(friendship -> friendship.getStatus() == FriendStatus.ACCEPTED)
                .orElse(false);

        if (!areFriends) {
            throw new UnAuthorisedException("You can only add users who are your friends.");
        }

        ChatParticipant newParticipant = ChatParticipant.builder()
                .chat(chat)
                .user(userToAdd)
                .chatRole(ChatRole.MEMBER)
                .joinedAt(LocalDateTime.now())
                .build();
        chat.getParticipants().add(newParticipant);
        chatRepository.save(chat);

        log.info("User {} added to chat {}", userId, chatId);
    }

    @Transactional
    @Override
    public void removeParticipant(Long chatId, Long userId) {
        User currentUser = getCurrentUser();
        log.info("User {} removing participant {} from chat {}", currentUser.getId(), userId, chatId);

        Chat chat = getById(chatId);
        if (!chat.getType().equals(ChatType.GROUP)) {
            throw new UnAuthorisedException("Can't remove participants from non-GROUP chats");
        }

        if (!checkAdmin(chat, currentUser)) {
            throw new AccessDeniedException("Only admins can remove participants");
        }

        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!checkParticipant(chatId, userToRemove.getId())) {
            throw new IllegalStateException("User with id: " + userToRemove.getId() + " does not belong to chat " + chatId);
        }

        ChatParticipant participantToRemove = chat.getParticipants().stream()
                .filter(p -> p.getUser().getId().equals(userToRemove.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Participant not found"));
        long countAdmin = chat
                .getParticipants()
                .stream()
                .filter(x -> x.getChatRole() == ChatRole.ADMIN)
                .count() ;

        if (participantToRemove.getChatRole() == ChatRole.ADMIN && countAdmin <= 1) {
            throw new UnAuthorisedException("Can't remove the only ADMIN of the chat");
        }
        chat.getParticipants().remove(participantToRemove);
        chatRepository.save(chat);

        log.info("User {} removed from chat {}", userId, chatId);
    }

    @Override
    public List<ChatParticipantDto> getAllParticipants(Long chatId) {
        User currentUser = getCurrentUser();

        if (!checkParticipant(chatId, currentUser.getId())) {
            throw new AccessDeniedException("User with id: " + currentUser.getId() + " does not belong to chat " + chatId);
        }

        Chat chat = getById(chatId);

        return chat.getParticipants().stream()
                .map(p -> modelMapper.map(p, ChatParticipantDto.class))
                .toList();
    }

    @Override
    public boolean isUserParticipant(Long chatId, Long userId) {
        getById(chatId);
        return checkParticipant(chatId, userId);
    }

    @Override
    public boolean isUserAdmin(Long chatId, Long userId) {
        Chat chat = getById(chatId);

        if (!checkParticipant(chatId, userId)) {
            throw new AccessDeniedException("User with id: " + userId + " does not belong to chat " + chatId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return checkAdmin(chat, user);
    }

    @Transactional
    @Override
    public void updateParticipantRole(Long chatId, Long userId) {
        User currentUser = getCurrentUser();
        Chat chat = getById(chatId);

        if (!checkAdmin(chat, currentUser)) {
            throw new AccessDeniedException("Only admins can update participants");
        }

        ChatParticipant chatParticipant = chat.getParticipants().stream()
                .filter(p -> p.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        chatParticipant.setChatRole(
                chatParticipant.getChatRole() == ChatRole.ADMIN ? ChatRole.MEMBER : ChatRole.ADMIN);

        chatParticipantRepository.save(chatParticipant);
    }

    private Chat getById(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + chatId));
    }

    private boolean checkAdmin(Chat chat, User user) {
        return chat.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(user.getId()) && p.getChatRole() == ChatRole.ADMIN);
    }

    private boolean checkParticipant(Long chatId, Long userId) {
        return getById(chatId).getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(userId));
    }
}
