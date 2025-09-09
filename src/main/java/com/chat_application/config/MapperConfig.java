package com.chat_application.config;

import com.chat_application.dto.AttachmentDto;
import com.chat_application.dto.FriendRequestDto;
import com.chat_application.dto.MessageSummaryDto;
import com.chat_application.entity.Attachment;
import com.chat_application.entity.Friendship;
import com.chat_application.entity.Message;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class MapperConfig {
    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(Friendship.class, FriendRequestDto.class).addMappings(mapper ->
                mapper.map(src -> src.getUser2().getId(), FriendRequestDto::setUser2)
        );

        modelMapper.typeMap(Attachment.class, AttachmentDto.class).addMappings(mapper ->
                mapper.map(src -> src.getMessage().getId(), AttachmentDto::setMessageId));

        modelMapper.typeMap(Message.class, MessageSummaryDto.class).addMappings(mapper -> {
            mapper.map(Message::getId, MessageSummaryDto::setId);
            mapper.map(src -> src.getChat().getId(), MessageSummaryDto::setChatId); // if you add chatId field
            mapper.map(Message::getSender, MessageSummaryDto::setSender);
            mapper.map(Message::getMessageType, MessageSummaryDto::setMessageType);
            mapper.map(Message::getContent, MessageSummaryDto::setContent);
            mapper.map(Message::getCreatedAt, MessageSummaryDto::setCreatedAt);
            mapper.using(ctx -> {
                Message message = (Message) ctx.getSource();
                return message.getAttachments() != null && !message.getAttachments().isEmpty();
            }).map(src -> src, MessageSummaryDto::setHasAttachments);
        });

        return modelMapper;
        }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

