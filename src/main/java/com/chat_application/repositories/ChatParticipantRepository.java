package com.chat_application.repositories;

import com.chat_application.entity.ChatParticipant;
import com.chat_application.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant,Long> {

}
