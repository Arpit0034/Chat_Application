package com.chat_application.services;

import com.chat_application.dto.UserSummaryDto;
import com.chat_application.entity.*;
import com.chat_application.entity.enums.MessageSendStatus;
import com.chat_application.entity.enums.MessageStatus;
import com.chat_application.exception.ResourceNotFoundException;
import com.chat_application.repositories.ChatRepository;
import com.chat_application.repositories.MessageReadRepository;
import com.chat_application.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.chat_application.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
public class MessageReadServiceImpl implements MessageReadService {

    private final MessageRepository messageRepository;
    private final MessageReadRepository messageReadRepository;
    private final ModelMapper modelMapper;
    private final ChatRepository chatRepository;

    @Transactional
    @Override
    public void markAsRead(Long messageId) {
        User user = getCurrentUser();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        if (!MessageSendStatus.DELIVERED.equals(message.getSendStatus())) {
            throw new AccessDeniedException("Cannot mark message as read before it is delivered");
        }

        boolean isSender = message.getSender().getId().equals(user.getId());
        boolean canRead = !isSender && message.getReceiverMessageStatus() == MessageStatus.VISIBLE;

        if (!canRead) {
            throw new AccessDeniedException("Message is deleted for you, cannot mark as read");
        }

        Optional<MessageRead> existingRead = messageReadRepository.findByMessageIdAndUserId(messageId, user.getId());
        if (existingRead.isEmpty()) {
            MessageRead messageRead = new MessageRead();
            messageRead.setMessage(message);
            messageRead.setUser(user);
            messageRead.setReadAt(LocalDateTime.now());
            messageReadRepository.save(messageRead);
        }
    }

    @Override
    public List<UserSummaryDto> getReadsByMessage(Long messageId) {
        User user = getCurrentUser();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        if (!MessageSendStatus.DELIVERED.equals(message.getSendStatus())) {
            throw new AccessDeniedException("Cannot access users list before message is delivered");
        }

        boolean isSender = message.getSender().getId().equals(user.getId());
        boolean canView = !isSender && message.getReceiverMessageStatus() == MessageStatus.VISIBLE;

        if (!canView) {
            throw new AccessDeniedException("Message is deleted for you, cannot get users list");
        }

        List<MessageRead> messageReads = message.getMessageReads();
        return messageReads.stream()
                .map(mr -> modelMapper.map(mr.getUser(), UserSummaryDto.class))
                .toList();
    }

    @Override
    public long getUnreadCountForMessage(Long messageId) {
        User user = getCurrentUser();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        if (!MessageSendStatus.DELIVERED.equals(message.getSendStatus())) {
            throw new AccessDeniedException("Cannot access unread user count before message is delivered");
        }

        boolean isSender = message.getSender().getId().equals(user.getId());
        boolean canView = isSender && message.getSenderMessageStatus()== MessageStatus.VISIBLE;

        if (!canView) {
            throw new AccessDeniedException("Message is deleted for you, cannot get unread user count");
        }

        Chat chat = message.getChat();
        List<User> allParticipants = chat.getParticipants().stream()
                .map(ChatParticipant::getUser)
                .toList();

        List<Long> readUserIds = message.getMessageReads().stream()
                .map(mr -> mr.getUser().getId())
                .toList();

        return allParticipants.stream()
                .filter(u -> !readUserIds.contains(u.getId()))
                .count();
    }

    @Override
    public boolean hasUserReadMessage(Long messageId, Long userId) {
        User currentUser = getCurrentUser();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        if (!MessageSendStatus.DELIVERED.equals(message.getSendStatus())) {
            throw new AccessDeniedException("Cannot access read status before message is delivered");
        }

        boolean isSender = message.getSender().getId().equals(currentUser.getId());
        boolean canView = isSender && message.getReceiverMessageStatus() == MessageStatus.VISIBLE ;

        if (!canView) {
            throw new AccessDeniedException("Message is deleted for you, cannot check read status");
        }

        return message.getMessageReads().stream()
                .anyMatch(mr -> mr.getUser().getId().equals(userId));
    }

    @Transactional
    @Override
    public void markAllAsReadInChat(Long chatId) {
        User user = getCurrentUser();
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + chatId));

        boolean isParticipant = chat.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(user.getId()));
        if (!isParticipant) {
            throw new AccessDeniedException("You do not have access to this chat");
        }

        List<Message> messagesToMarkRead = chat.getMessages().stream()
                .filter(msg -> !msg.getSender().getId().equals(user.getId())
                        && MessageSendStatus.DELIVERED.equals(msg.getSendStatus())
                        && msg.getReceiverMessageStatus() == MessageStatus.VISIBLE)
                .toList();

        for (Message message : messagesToMarkRead) {
            boolean alreadyRead = message.getMessageReads().stream()
                    .anyMatch(mr -> mr.getUser().getId().equals(user.getId()));
            if (!alreadyRead) {
                MessageRead messageRead = new MessageRead();
                messageRead.setMessage(message);
                messageRead.setUser(user);
                messageRead.setReadAt(LocalDateTime.now());
                messageReadRepository.save(messageRead);
            }
        }
    }
}
