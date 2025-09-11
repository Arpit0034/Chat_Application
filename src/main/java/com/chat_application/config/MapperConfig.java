package com.chat_application.config;

import com.chat_application.dto.AttachmentDto;
import com.chat_application.dto.FriendRequestDto;
import com.chat_application.dto.MessageDto;
import com.chat_application.dto.MessageSummaryDto;
import com.chat_application.entity.Attachment;
import com.chat_application.entity.Friendship;
import com.chat_application.entity.Message;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Explicit mapping: Message -> MessageDto
        modelMapper.addMappings(new PropertyMap<Message, MessageDto>() {
            @Override
            protected void configure() {
                // Map normal fields
                map().setId(source.getId());
                map().setContent(source.getContent());
                map().setCreatedAt(source.getCreatedAt());

                // Skip sender (to prevent recursive/nested mapping errors)
                skip(destination.getSender());
            }
        });

        return modelMapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}


