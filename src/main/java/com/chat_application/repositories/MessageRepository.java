package com.chat_application.repositories;

import com.chat_application.entity.Message;
import com.chat_application.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message,Long> {
    List<Message> findBySender(User user);

    @Query("""
        SELECT m FROM Message m
        JOIN m.chat c
        JOIN c.participants p
        WHERE m.chat.id = :chatId
          AND p.user.id = :userId
          AND (
               (m.sender.id = :userId AND m.senderMessageStatus = 'VISIBLE') OR
               (m.sender.id <> :userId AND m.receiverMessageStatus = 'VISIBLE')
              )
        ORDER BY m.createdAt ASC
        """)
    Page<Message> findVisibleMessagesForUser(@Param("chatId") Long chatId,
                                             @Param("userId") Long userId,
                                             Pageable pageable);

    List<Message> findAllByChatId(Long chatId);
}
