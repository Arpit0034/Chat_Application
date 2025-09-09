package com.chat_application.dto;

import com.chat_application.entity.enums.AttachmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentCreateRequestDto {
    @NotBlank(message = "File URL is required")
    private String fileUrl;

    @NotNull(message = "File type is required")
    private AttachmentType fileType;

    @NotNull(message = "File size is required")
    private Long size;
}
