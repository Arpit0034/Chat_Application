package com.chat_application.repositories;

import com.chat_application.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface AttachmentRepository extends JpaRepository<Attachment,Long> {
}
