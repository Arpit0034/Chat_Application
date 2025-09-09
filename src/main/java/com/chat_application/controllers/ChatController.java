package com.chat_application.controllers;

import com.chat_application.dto.ChatCreateRequestDto;
import com.chat_application.dto.ChatDto;
import com.chat_application.dto.ChatParticipantDto;
import com.chat_application.dto.ChatSummaryDto;
import com.chat_application.services.ChatService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService ;

    @PostMapping("/createChat")
    public ResponseEntity<ChatDto> createChat(@RequestBody ChatCreateRequestDto chatCreateRequestDto){
        return new ResponseEntity<>(chatService.createChat(chatCreateRequestDto), HttpStatus.CREATED) ;
    }

    @GetMapping("/getChatByName/{chatName}")
    public ResponseEntity<List<ChatSummaryDto>> getChatByName(@PathVariable String name){
        return new ResponseEntity<>(chatService.getChatByName(name),HttpStatus.FOUND) ;
    }

    @GetMapping("/getAllChats/{page}/{size}")
    public ResponseEntity<?> getAllChats(@Positive @PathVariable int page ,@Positive @PathVariable int size){
        return new ResponseEntity<>(chatService.getAllChats(page,size),HttpStatus.FOUND) ;
    }

    @DeleteMapping("/deleteChat/{chatId}")
    public ResponseEntity<Void> removeParticipant(@Positive @PathVariable Long chatId){
        chatService.deleteChat(chatId);
        return ResponseEntity.noContent().build() ;
    }

    @DeleteMapping("/leaveChat/{chatId}")
    public ResponseEntity<Void> leaveParticipant(@Positive @PathVariable Long chatId){
        chatService.leaveChat(chatId);
        return ResponseEntity.noContent().build() ;
    }

}
