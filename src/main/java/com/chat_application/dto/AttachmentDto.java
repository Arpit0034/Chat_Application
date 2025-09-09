package com.chat_application.dto;

import com.chat_application.entity.enums.AttachmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentDto {
    private Long id;
    private Long messageId;
    private String fileUrl;
    private AttachmentType fileType;
    private Long size;
    private LocalDateTime createdAt;
}
