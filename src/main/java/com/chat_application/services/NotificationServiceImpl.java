package com.chat_application.services;

import com.chat_application.dto.NotificationDto;
import com.chat_application.dto.NotificationSummaryDto;
import com.chat_application.entity.Chat;
import com.chat_application.entity.Message;
import com.chat_application.entity.Notification;
import com.chat_application.entity.User;
import com.chat_application.entity.enums.NotificationType;
import com.chat_application.exception.ResourceNotFoundException;
import com.chat_application.repositories.ChatRepository;
import com.chat_application.repositories.MessageRepository;
import com.chat_application.repositories.NotificationRepository;
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

import static com.chat_application.util.AppUtils.getCurrentUser;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    private final ModelMapper modelMapper ;
    private final ChatRepository chatRepository ;
    private final NotificationRepository notificationRepository ;
    private final MessageRepository messageReadRepository ;
    private final SimpMessagingTemplate simpMessagingTemplate ;

    @Transactional
    @Override
    public NotificationDto createNotification(Long chatId, Long messageId, NotificationType type) {
        User user = getCurrentUser() ;
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: "+chatId)) ;
        Message message = messageReadRepository.findById(messageId).orElseThrow(() -> new ResourceNotFoundException("Message not found with id: "+messageId)) ;
        Notification notification = Notification.builder()
                .user(user)
                .chat(chat)
                .isRead(false)
                .message(message)
                .type(type)
                .build() ;
        notificationRepository.save(notification) ;
        NotificationDto notificationDto = modelMapper.map(notification,NotificationDto.class) ;
        simpMessagingTemplate.convertAndSend("/topic/chat/" + chatId + "/notifications",notificationDto);
        return notificationDto ;
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
        notificationRepository.delete(notification); ;
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
