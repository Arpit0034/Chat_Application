package com.chat_application.controllers;

import com.chat_application.dto.ChatParticipantDto;
import com.chat_application.services.ChatParticipantService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ChatParticipantController {
    private final ChatParticipantService chatParticipantService ;

    @PatchMapping("/addParticipant/{chatId}/{userId}")
    public ResponseEntity<Void> addParticipant(@Positive @PathVariable Long chatId , @Positive @PathVariable Long userId){
        chatParticipantService.addParticipant(chatId,userId);
        return ResponseEntity.noContent().build() ;
    }

    @GetMapping("/getAllParticipants/{chatId}")
    public ResponseEntity<List<ChatParticipantDto>> getAllParticipants(@Positive @PathVariable Long chatId){
        return new ResponseEntity<>(chatParticipantService.getAllParticipants(chatId), HttpStatus.FOUND) ;
    }

    @DeleteMapping("/removeParticipant/{chatId}/{userId}")
    public ResponseEntity<Void> removeParticipant(@Positive @PathVariable Long chatId ,@Positive @PathVariable Long userId){
        chatParticipantService.removeParticipant(chatId,userId);
        return ResponseEntity.noContent().build() ;
    }

    @GetMapping("/isUserParticipant/{chatId}/{userId}")
    public ResponseEntity<Boolean> isUserParticipant(@PathVariable @Positive Long chatId ,@PathVariable Long userId){
        return ResponseEntity.ok().body(chatParticipantService.isUserParticipant(chatId,userId)) ;
    }

    @GetMapping("/isUserParticipant/{chatId}/{userId}")
    public ResponseEntity<Boolean> isUserAdmin(@PathVariable @Positive Long chatId ,@PathVariable @Positive Long userId){
        return ResponseEntity.ok().body(chatParticipantService.isUserAdmin(chatId,userId)) ;
    }

    @PatchMapping("/updateParticipantRole/{chatId}/{userId}")
    public ResponseEntity<Void> updateParticipantRole(@PathVariable @Positive Long chatId ,@PathVariable @Positive Long userId){
        chatParticipantService.updateParticipantRole(chatId,userId);
        return ResponseEntity.noContent().build() ;
    }
}
