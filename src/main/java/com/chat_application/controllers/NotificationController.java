package com.chat_application.controllers;

import com.chat_application.dto.NotificationDto;
import com.chat_application.dto.NotificationSummaryDto;
import com.chat_application.entity.enums.NotificationType;
import com.chat_application.services.NotificationService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService ;

    @PostMapping("/createNotification/{chatId}/{messageId}/{type}")
    public ResponseEntity<NotificationDto> createNotification(@Positive @PathVariable Long chatId ,@Positive @PathVariable Long messageId, @PathVariable NotificationType notificationType){
        return new ResponseEntity<>(notificationService.createNotification(chatId,messageId,notificationType), HttpStatus.CREATED) ;
    }

    @GetMapping("/getNotifications/{page}/{size}")
    public ResponseEntity<List<NotificationSummaryDto>> getNotifications(@Positive @PathVariable int page ,@Positive @PathVariable int size){
        return new ResponseEntity<>(notificationService.getNotificationsForUser(size,page),HttpStatus.FOUND) ;
    }

    @PatchMapping("/markAsRead/{notificationId}")
    public ResponseEntity<Void> markAsRead(@Positive @PathVariable Long notificationId){
        notificationService.markAsRead(notificationId) ;
        return ResponseEntity.noContent().build() ;
    }

    @GetMapping("/getUnreadNotificationsCount")
    public ResponseEntity<Long> getUnreadNotificationsCount(){
        return ResponseEntity.ok().body(notificationService.getUnreadNotificationsCount()) ;
    }

    @DeleteMapping("/deleteNotification/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@Positive @PathVariable Long notificationId){
        notificationService.deleteNotification(notificationId) ;
        return ResponseEntity.noContent().build() ;
    }

    @DeleteMapping("/deleteAllNotification")
    public ResponseEntity<Void> deleteAllNotification(){
        notificationService.deleteAllNotification() ;
        return ResponseEntity.noContent().build() ;
    }
}
