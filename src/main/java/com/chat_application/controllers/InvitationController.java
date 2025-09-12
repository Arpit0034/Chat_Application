package com.chat_application.controllers;

import com.chat_application.dto.InvitationDto;
import com.chat_application.services.InvitationService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/sendInvitation/{receiverId}/{chatId}")
    public ResponseEntity<InvitationDto> sendInvitation(@Positive @PathVariable Long receiverId, @Positive @PathVariable Long chatId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invitationService.sendInvitation(receiverId, chatId));
    }

    @PatchMapping("/acceptInvitation/{invitationId}")
    public ResponseEntity<InvitationDto> acceptInvitation(@Positive @PathVariable Long invitationId) {
        return ResponseEntity.ok().body(invitationService.acceptInvitation(invitationId));
    }

    @PatchMapping("/rejectInvitation/{invitationId}")
    public ResponseEntity<InvitationDto> rejectInvitation(@Positive @PathVariable Long invitationId) {
        return ResponseEntity.ok().body(invitationService.rejectInvitation(invitationId));
    }
}