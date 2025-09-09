package com.chat_application.services;

import com.chat_application.dto.LoginDto;
import com.chat_application.dto.SignUpDto;
import com.chat_application.dto.UserDto;
import com.chat_application.entity.User;

public interface AuthService {

    public UserDto signUp(SignUpDto signUpDto);
    String[] login(LoginDto loginDto);
    String refreshToken(String refreshToken);
}
