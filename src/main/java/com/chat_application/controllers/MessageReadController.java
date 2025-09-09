package com.chat_application.controllers;

import com.chat_application.dto.UserSummaryDto;
import com.chat_application.services.MessageReadService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class MessageReadController {

    private final MessageReadService messageReadService ;

    @PatchMapping("/markAsRead/{messageId}")
    public ResponseEntity<Void> markAsRead(@Positive @PathVariable Long messageId){
        messageReadService.markAsRead(messageId);
        return ResponseEntity.noContent().build() ;
    }

    @GetMapping("/getUnreadCount/{messageId}")
    public ResponseEntity<Long> getUnreadCount(@Positive @PathVariable Long messageId){
        return ResponseEntity.ok().body(messageReadService.getUnreadCountForMessage(messageId)) ;
    }

    @GetMapping("/getReadByMessage/{messageId}")
    public ResponseEntity<List<UserSummaryDto>> getReadByMessage(@Positive @PathVariable Long messageId){
        return new ResponseEntity<>(messageReadService.getReadsByMessage(messageId), HttpStatus.FOUND) ;
    }

    @PatchMapping("/markAllAsReadInChat/{chatId}")
    public ResponseEntity<Void> markAllAsReadInChat(@Positive @PathVariable Long chatId){
        messageReadService.markAllAsReadInChat(chatId);
        return ResponseEntity.noContent().build() ;
    }

    @GetMapping("/hasUserReadMessage/{messageId}/{userId}")
    public ResponseEntity<Boolean> hasUserReadMessage(@Positive @PathVariable Long messageId , @Positive @PathVariable Long userId){
        return ResponseEntity.ok().body(messageReadService.hasUserReadMessage(messageId,userId)) ;
    }

}
