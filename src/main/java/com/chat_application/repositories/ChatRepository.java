package com.chat_application.repositories;

import com.chat_application.entity.Chat;
import com.chat_application.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Long> {
    List<Chat> findByName(String name);

    Page<Chat> findByParticipantsUserId(User user, Pageable pageable);
}
