package com.chat_application.services;

import com.chat_application.dto.AttachmentCreateRequestDto;
import com.chat_application.dto.AttachmentDto;

import java.util.List;

public interface AttachmentService{
    AttachmentDto addAttachmentToMessage(Long messageId, AttachmentCreateRequestDto attachmentDto);
    void removeAttachment(Long attachmentId);
    AttachmentDto getAttachmentById(Long attachmentId);
    List<AttachmentDto> getAttachmentsForMessage(Long messageId);
}
