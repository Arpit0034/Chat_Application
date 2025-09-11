package com.chat_application.services;

import com.chat_application.dto.InvitationDto;
import com.chat_application.entity.Chat;
import com.chat_application.entity.ChatParticipant;
import com.chat_application.entity.Invitation;
import com.chat_application.entity.User;
import com.chat_application.entity.enums.ChatRole;
import com.chat_application.entity.enums.ChatType;
import com.chat_application.entity.enums.InvitationStatus;
import com.chat_application.exception.ResourceNotFoundException;
import com.chat_application.exception.UnAuthorisedException;
import com.chat_application.repositories.ChatRepository;
import com.chat_application.repositories.InvitationRepository;
import com.chat_application.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.chat_application.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService{

    private final ModelMapper modelMapper ;
    private final InvitationRepository invitationRepository ;
    private final UserRepository userRepository ;
    private final ChatRepository chatRepository ;

    @Override
    public InvitationDto sendInvitation(Long receiverId, Long chatId) {
        User user = getCurrentUser() ;
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ResourceNotFoundException("Chat not found with id : "+chatId)) ;
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new ResourceNotFoundException("User not found with id : "+receiverId)) ;

        boolean checkCurrentUser = chat.getParticipants().stream().anyMatch(x ->
                x.getUser().getId().equals(user.getId())
                && x.getChatRole().equals(ChatRole.ADMIN)
        ) && chat.getType().equals(ChatType.GROUP);

        boolean checkReceiver = chat.getParticipants().stream().anyMatch(x -> !x.getUser().getId().equals(receiverId)) ;
        if(!checkCurrentUser && !checkReceiver){
            throw new UnAuthorisedException("Access Denied for user with id : "+user.getId()) ;
        }
        Invitation invitation = Invitation
                .builder()
                .invitationStatus(InvitationStatus.PENDING)
                .chat(chat)
                .sender(user)
                .receiver(receiver)
                .build() ;
        return modelMapper.map(invitation,InvitationDto.class) ;
    }

    @Transactional
    @Override
    public InvitationDto acceptInvitation(Long invitationId) {
        User currentUser = getCurrentUser();

        // 1. Fetch invitation
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invitation not found with id: " + invitationId));

        Chat chat = invitation.getChat();
        if (chat == null) {
            throw new ResourceNotFoundException("Chat not found for invitation: " + invitationId);
        }

        // 2. Ensure the current user is the receiver
        if (!invitation.getReceiver().getId().equals(currentUser.getId())) {
            throw new UnAuthorisedException("Access Denied for user with id: " + currentUser.getId());
        }

        // 3. Mark invitation as accepted
        invitation.setInvitationStatus(InvitationStatus.ACCEPTED);
        invitation.setRespondedAt(LocalDateTime.now());

        // 4. Add participant explicitly (skip admin check)
        boolean alreadyParticipant = chat.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(currentUser.getId()));

        if (!alreadyParticipant) {
            ChatParticipant participant = ChatParticipant.builder()
                    .chat(chat)
                    .user(currentUser)
                    .chatRole(ChatRole.MEMBER)
                    .joinedAt(LocalDateTime.now())
                    .build();
            chat.getParticipants().add(participant);
        }

        invitation = invitationRepository.save(invitation);
        chatRepository.save(chat);

        return modelMapper.map(invitation, InvitationDto.class);
    }

    @Transactional
    @Override
    public InvitationDto rejectInvitation(Long invitationId) {
        User currentUser = getCurrentUser();

        // 1. Fetch invitation
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invitation not found with id: " + invitationId));

        Chat chat = invitation.getChat();
        if (chat == null) {
            throw new ResourceNotFoundException("Chat not found for invitation: " + invitationId);
        }

        // 2. Ensure the current user is the receiver
        if (!invitation.getReceiver().getId().equals(currentUser.getId())) {
            throw new UnAuthorisedException("Access Denied for user with id: " + currentUser.getId());
        }

        // 3. Mark invitation as rejected
        invitation.setInvitationStatus(InvitationStatus.REJECTED);
        invitation.setRespondedAt(LocalDateTime.now());

        // 4. Save changes
        invitation = invitationRepository.save(invitation);

        return modelMapper.map(invitation, InvitationDto.class);
    }
}
