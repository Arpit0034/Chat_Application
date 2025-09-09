package com.chat_application.repositories;

import com.chat_application.entity.MessageRead;
import com.chat_application.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageReadRepository extends JpaRepository<MessageRead,Long> {
    Optional<MessageRead> findByMessageIdAndUserId(Long messageId, Long userId);
}
