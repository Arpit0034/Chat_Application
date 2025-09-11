package com.chat_application.services;

import com.chat_application.dto.InvitationDto;
import com.chat_application.entity.Invitation;

public interface InvitationService {
    public InvitationDto sendInvitation(Long receiverId , Long chat) ;
    public InvitationDto acceptInvitation(Long invitationId) ;
    public InvitationDto rejectInvitation(Long invitationId) ;
}
