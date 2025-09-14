package com.chat_application.services;

import com.chat_application.dto.NotificationCreateDto;
import com.chat_application.dto.NotificationDto;
import com.chat_application.dto.NotificationSummaryDto;
import com.chat_application.entity.*;
import com.chat_application.entity.enums.ChatRole;
import com.chat_application.entity.enums.NotificationType;
import com.chat_application.exception.ResourceNotFoundException;
import com.chat_application.repositories.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.chat_application.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    private final ModelMapper modelMapper ;
    private final ChatRepository chatRepository ;
    private final NotificationRepository notificationRepository ;
    private final MessageRepository messageRepository ;
    private final SimpMessagingTemplate simpMessagingTemplate ;
    private final UserRepository userRepository ;
    private final FriendshipRepository friendshipRepository ;

    @Transactional
    @Override
    public NotificationDto createNotification(NotificationCreateDto createNotificationDto) {
        User currentUser = getCurrentUser();

        if (createNotificationDto.getType().equals(NotificationType.NEW_MESSAGE)) {
            Chat chat = chatRepository.findById(createNotificationDto.getChatId())
                    .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + createNotificationDto.getChatId()));

            boolean checkMessage = chat.getMessages().stream()
                    .anyMatch(x -> x.getId().equals(createNotificationDto.getMessageId()));
            if (!checkMessage) {
                throw new AccessDeniedException("Message is not part of chat");
            }

            Message message = messageRepository.findById(createNotificationDto.getMessageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + createNotificationDto.getMessageId()));

            if (!message.getSender().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Can't create notification");
            }

            List<User> recipients = chat.getParticipants().stream()
                    .map(ChatParticipant::getUser)
                    .filter(user -> !user.getId().equals(currentUser.getId()))
                    .toList();

            for (User recipient : recipients) {
                Notification notification = Notification.builder()
                        .type(NotificationType.NEW_MESSAGE)
                        .message(message)
                        .isRead(false)
                        .chat(chat)
                        .user(recipient)
                        .content(null)
                        .build();

                notificationRepository.save(notification);
                NotificationDto notificationDto = modelMapper.map(notification, NotificationDto.class);
                simpMessagingTemplate.convertAndSend("/topic/user/" + recipient.getId() + "/notifications", notificationDto);
            }

            return recipients.isEmpty() ? null : modelMapper.map(
                    notificationRepository.findTopByTypeAndMessageIdOrderByCreatedAtDesc(NotificationType.NEW_MESSAGE, message.getId()),
                    NotificationDto.class);
        }

        if (createNotificationDto.getType().equals(NotificationType.GROUP_INVITE)) {
            Chat chat = chatRepository.findById(createNotificationDto.getChatId())
                    .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + createNotificationDto.getChatId()));

            boolean isAdmin = chat.getParticipants().stream()
                    .anyMatch(x -> x.getUser().getId().equals(currentUser.getId()) && x.getChatRole().equals(ChatRole.ADMIN));
            if (!isAdmin) {
                throw new AccessDeniedException("Can't create notification");
            }

            User receiver = userRepository.findById(createNotificationDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + createNotificationDto.getUserId()));

            boolean isPartOfChat = chat.getParticipants().stream()
                    .anyMatch(x -> x.getUser().getId().equals(receiver.getId()));
            if (isPartOfChat) {
                throw new AccessDeniedException("Can't send group invite request");
            }

            Notification notification = Notification.builder()
                    .content("You have been invited to join group " + chat.getName())
                    .chat(chat)
                    .message(null)
                    .user(receiver)
                    .type(NotificationType.GROUP_INVITE)
                    .isRead(false)
                    .build();

            notificationRepository.save(notification);
            NotificationDto notificationDto = modelMapper.map(notification, NotificationDto.class);
            simpMessagingTemplate.convertAndSend("/topic/user/" + receiver.getId() + "/notifications", notificationDto);
            return notificationDto;
        }

        if (createNotificationDto.getType().equals(NotificationType.FRIEND_REQUEST)) {
            User receiver = userRepository.findById(createNotificationDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + createNotificationDto.getUserId()));

            // Check if friendship already exists or request is pending
            Optional<Friendship> existingFriendship = friendshipRepository.findByUser1AndUser2(currentUser, receiver);
            if (existingFriendship.isPresent()) {
                throw new AccessDeniedException("Friendship request already exists or users are already friends");
            }

            Notification notification = Notification.builder()
                    .content(currentUser.getName() + " sent you a friend request")
                    .chat(null)
                    .message(null)
                    .user(receiver)
                    .type(NotificationType.FRIEND_REQUEST)
                    .isRead(false)
                    .build();

            notificationRepository.save(notification);
            NotificationDto notificationDto = modelMapper.map(notification, NotificationDto.class);
            simpMessagingTemplate.convertAndSend("/topic/user/" + receiver.getId() + "/notifications", notificationDto);
            return notificationDto;
        }

        throw new IllegalArgumentException("Unsupported notification type: " + createNotificationDto.getType());
    }


    @Override
    public List<NotificationSummaryDto> getNotificationsForUser(int page, int size) {
        User user = getCurrentUser() ;
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").descending()) ;
        Page<Notification> notifications = notificationRepository.findByUserId(user.getId(),pageable) ;
        return notifications
                .stream()
                .map(notification ->
                    modelMapper.map(notification, NotificationSummaryDto.class)
                )
                .toList() ;
    }

    @Transactional
    @Override
    public void markAsRead(Long notificationId) {
        User user = getCurrentUser() ;
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new ResourceNotFoundException("Notification with id :"+notificationId+" not found")) ;
        if(!notification.getUser().getId().equals(user.getId())){
            throw new AccessDeniedException("Notification with id : "+notificationId+" not belong to user with id: "+user.getId()) ;
        }
        notification.setIsRead(true);
        notificationRepository.save(notification) ;
    }

    @Transactional
    @Override
    public void deleteNotification(Long notificationId) {
        User user = getCurrentUser() ;
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new ResourceNotFoundException("Notification with id :"+notificationId+" not found")) ;
        if(!notification.getUser().getId().equals(user.getId())){
            throw new AccessDeniedException("Notification with id : "+notificationId+" not belong to user with id: "+user.getId()) ;
        }
        notificationRepository.delete(notification);
    }

    @Transactional
    @Override
    public void deleteAllNotification(){
        User user = getCurrentUser() ;
        notificationRepository.deleteAllByUserId(user.getId());
    }

    @Override
    public long getUnreadNotificationsCount() {
        User user = getCurrentUser() ;
        return notificationRepository.countByUserIdAndIsReadFalse(user.getId()) ;
    }
}
