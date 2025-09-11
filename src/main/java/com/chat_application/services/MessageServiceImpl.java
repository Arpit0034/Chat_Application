package com.chat_application.services;

import com.chat_application.dto.*;
import com.chat_application.entity.*;
import com.chat_application.entity.enums.MessageStatus;
import com.chat_application.exception.ResourceNotFoundException;
import com.chat_application.repositories.AttachmentRepository;
import com.chat_application.repositories.ChatRepository;
import com.chat_application.repositories.MessageReadRepository;
import com.chat_application.repositories.MessageRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.chat_application.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final ModelMapper modelMapper;
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final AttachmentRepository attachmentRepository;
    private final MessageReadRepository messageReadRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    @Override
    public MessageSummaryDto createMessage(MessageCreateRequestDto dto) {
        User currentUser = getCurrentUser();

        Chat chat = chatRepository.findById(dto.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with id: " + dto.getChatId()));

        Message message = new Message();
        message.setChat(chat);
        message.setSender(currentUser);
        message.setMessageType(dto.getMessageType());
        message.setContent(dto.getContent());
        message.setSenderMessageStatus(MessageStatus.VISIBLE);
        message.setReceiverMessageStatus(MessageStatus.VISIBLE);

        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            List<Attachment> attachments = dto.getAttachments()
                    .stream()
                    .map(attDto -> modelMapper.map(attDto, Attachment.class))
                    .toList();
            message.setAttachments(attachments);
            attachments.forEach(att -> att.setMessage(message));
        }

        Message savedMessage = messageRepository.save(message);
        MessageSummaryDto messageSummaryDto = modelMapper.map(savedMessage,MessageSummaryDto.class) ;
        simpMessagingTemplate.convertAndSend("/topic/chat/"+chat.getId(),messageSummaryDto);
        return messageSummaryDto ;
    }

    @Override
    public PagedResponseDto<MessageSummaryDto> getChatMessages(Long chatId, int page, int size) {
        User currentUser = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));

        Page<Message> messagePage = messageRepository.findVisibleMessagesForUser(chatId, currentUser.getId(), pageable);

        List<MessageSummaryDto> messages = messagePage.getContent()
                .stream()
                .map(message -> modelMapper.map(message, MessageSummaryDto.class))
                .collect(Collectors.toList());

        PagedResponseDto<MessageSummaryDto> response = new PagedResponseDto<>();
        response.setContent(messages);
        response.setPage(messagePage.getNumber());
        response.setSize(messagePage.getSize());
        response.setTotalElements(messagePage.getTotalElements());
        response.setTotalPages(messagePage.getTotalPages());
        response.setFirst(messagePage.isFirst());
        response.setLast(messagePage.isLast());
        response.setHasNext(messagePage.hasNext());
        response.setHasPrevious(messagePage.hasPrevious());

        return response;
    }

    @Transactional
    @Override
    public void deleteMessageForMe(Long messageId) {
        User currentUser = getCurrentUser();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        if (message.getSender().getId().equals(currentUser.getId())) {
            message.setSenderMessageStatus(MessageStatus.DELETE_FOR_ME);
        } else {
            message.setReceiverMessageStatus(MessageStatus.DELETE_FOR_ME);
        }

        messageRepository.save(message);
    }

    @Transactional
    @Override
    public void deleteMessageForEveryone(Long messageId) {
        User currentUser = getCurrentUser();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));

        if (!message.getSender().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only sender can delete message for everyone.");
        }
        messageRepository.delete(message);
    }

    @Transactional
    @Override
    public void deleteAllMessages(Long chatId) {
        User currentUser = getCurrentUser();

        List<Message> messages = messageRepository.findAllByChatId(chatId);

        for (Message message : messages) {
            if (message.getSender().getId().equals(currentUser.getId())) {
                message.setSenderMessageStatus(MessageStatus.DELETE_FOR_ME);
            } else {
                message.setReceiverMessageStatus(MessageStatus.DELETE_FOR_ME);
            }
        }

        messageRepository.saveAll(messages);
    }
}
