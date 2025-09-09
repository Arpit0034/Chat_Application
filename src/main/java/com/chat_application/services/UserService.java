package com.chat_application.services;

import com.chat_application.dto.*;
import com.chat_application.entity.User;

import java.time.LocalDateTime;
import java.util.List;


public interface UserService {

    UserDto updateUser(UserUpdateRequestDto userUpdateRequestDto) ;

    void deleteUser() ;

    void deactivateUser() ;

    void blockUser(Long friendId) ;

    void unblockUser(Long friendId) ;

    void activateUser() ;

    void updateOnlineStatus() ;

    LocalDateTime getLastSeen() ;

    void updateLastSeen() ;

    List<UserSummaryDto> searchUser(String name);

    List<UserDto> getAllUsers() ;

    User getUserById(Long userId);
}

