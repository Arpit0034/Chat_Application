package com.chat_application.controllers;

import com.chat_application.dto.AttachmentCreateRequestDto;
import com.chat_application.dto.AttachmentDto;
import com.chat_application.dto.MessageCreateRequestDto;
import com.chat_application.dto.MessageSummaryDto;
import com.chat_application.services.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService ;

    @PostMapping("/createMessage")
    public ResponseEntity<MessageSummaryDto> createMessage(@Valid @RequestBody MessageCreateRequestDto messageCreateRequestDto){
        return new ResponseEntity<>(messageService.createMessage(messageCreateRequestDto), HttpStatus.CREATED) ;
    }

    @DeleteMapping("/deleteForMe/{messageId}")
    public ResponseEntity<Void> deleteForMe(@Positive  @PathVariable Long messageId){
        messageService.deleteMessageForMe(messageId);
        return ResponseEntity.noContent().build() ;
    }

    @DeleteMapping("/deleteForEveryone/{messageId}")
    public ResponseEntity<Void> deleteForEveryone(@Positive @PathVariable Long messageId){
        messageService.deleteMessageForEveryone(messageId);
        return ResponseEntity.noContent().build() ;
    }

    @DeleteMapping("/deleteAll/{chatId}")
    public ResponseEntity<Void> deleteAll(@Positive @PathVariable Long chatId){
        messageService.deleteAllMessages(chatId);
        return ResponseEntity.noContent().build() ;
    }

    @GetMapping("/getChat/{chatId}/{page}/{size}")
    public ResponseEntity<?> getMessage(@Positive @PathVariable Long chatId , @PositiveOrZero @PathVariable int page , @Positive @PathVariable int size){
        return new ResponseEntity<>(messageService.getChatMessages(chatId,page,size),HttpStatus.FOUND) ;
    }

    @PatchMapping("/markMessageDelivered/{messageId}")
    public ResponseEntity<Void> markMessageDelivered(@Positive @PathVariable Long messageId){
        messageService.markMessageAsDelivered(messageId) ;
        return ResponseEntity.noContent().build() ;
    }
}
