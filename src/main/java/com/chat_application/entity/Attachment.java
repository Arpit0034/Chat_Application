package com.chat_application.entity;

import com.chat_application.entity.enums.AttachmentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    private String fileUrl;

    @Enumerated(EnumType.STRING)
    private AttachmentType fileType;

    private Long size;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

