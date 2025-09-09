package com.chat_application.dto;

import com.chat_application.entity.enums.ChatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSummaryDto {
    private Long id;
    private ChatType type;
    private String name;
    private Integer participantCount;
}
