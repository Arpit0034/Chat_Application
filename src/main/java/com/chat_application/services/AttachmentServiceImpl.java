package com.chat_application.services;

import com.chat_application.dto.AttachmentCreateRequestDto;
import com.chat_application.dto.AttachmentDto;
import com.chat_application.entity.Attachment;
import com.chat_application.entity.Chat;
import com.chat_application.entity.Message;
import com.chat_application.entity.User;
import com.chat_application.exception.ResourceNotFoundException;
import com.chat_application.repositories.AttachmentRepository;
import com.chat_application.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.chat_application.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService{

    private final ModelMapper modelMapper ;
    private final MessageRepository messageRepository ;
    private final AttachmentRepository attachmentRepository ;

    @Transactional
    @Override
    public AttachmentDto addAttachmentToMessage(Long messageId, AttachmentCreateRequestDto attachmentDto) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        Attachment attachment = modelMapper.map(attachmentDto, Attachment.class);
        attachment.setMessage(message);

        Attachment savedAttachment = attachmentRepository.save(attachment);

        return modelMapper.map(savedAttachment, AttachmentDto.class);
    }

    @Transactional
    @Override
    public void removeAttachment(Long attachmentId) {
        User user = getCurrentUser() ;
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));

        if (!attachment.getMessage().getSender().getId().equals(user.getId())) {
            throw new AccessDeniedException("Only sender can delete Attachment");
        }
        attachmentRepository.delete(attachment);
    }

    @Override
    public AttachmentDto getAttachmentById(Long attachmentId) {
        User currentUser = getCurrentUser();

        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + attachmentId));
        Chat chat = attachment.getMessage().getChat();

        boolean isParticipant = chat.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(currentUser.getId()));

        if (!isParticipant) {
            throw new AccessDeniedException("User does not have access to this attachment");
        }

        return modelMapper.map(attachment, AttachmentDto.class);
    }

    @Override
    public List<AttachmentDto> getAttachmentsForMessage(Long messageId) {
        User currentUser = getCurrentUser();
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new ResourceNotFoundException("Message not found with id: "+messageId)) ;

        Chat chat = message.getChat() ;
        boolean isParticipant = chat.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(currentUser.getId()));

        if (!isParticipant) {
            throw new AccessDeniedException("User does not have access to this attachments");
        }

        List<Attachment> attachments = message.getAttachments() ;
        return attachments.stream().map(attachment -> modelMapper.map(attachment, AttachmentDto.class)).toList() ;
    }
}
