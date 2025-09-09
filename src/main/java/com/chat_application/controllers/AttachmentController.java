package com.chat_application.controllers;

import com.chat_application.dto.AttachmentCreateRequestDto;
import com.chat_application.dto.AttachmentDto;
import com.chat_application.services.AttachmentService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService ;

    @PostMapping("/addAttachment/{messageId}")
    public ResponseEntity<AttachmentDto> addAttachment(@RequestBody AttachmentCreateRequestDto attachmentCreateRequestDto, @Positive @PathVariable Long messageId){
        return new ResponseEntity<>(attachmentService.addAttachmentToMessage(messageId,attachmentCreateRequestDto), HttpStatus.CREATED) ;
    }

    @DeleteMapping("/removeAttachment/{attachmentId}")
    public ResponseEntity<Void> removeAttachment(@Positive @PathVariable Long attachmentId){
        attachmentService.removeAttachment(attachmentId) ;
        return ResponseEntity.noContent().build() ;
    }

    @GetMapping("/getAttachmentById/{attachmentId}")
    public ResponseEntity<AttachmentDto> getAttachmentById(@Positive @PathVariable Long attachmentId){
        return new ResponseEntity<>(attachmentService.getAttachmentById(attachmentId),HttpStatus.FOUND) ;
    }

    @GetMapping("/getAllAttachmentsForMessage/{messageId}")
    public ResponseEntity<List<AttachmentDto>> getAllAttachmentsForMessage(@Positive @PathVariable Long messageId){
        return new ResponseEntity<>(attachmentService.getAttachmentsForMessage(messageId),HttpStatus.FOUND) ;
    }
}
