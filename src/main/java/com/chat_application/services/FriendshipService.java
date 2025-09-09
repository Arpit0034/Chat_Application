package com.chat_application.services;

import com.chat_application.dto.FriendRequestDto;
import com.chat_application.dto.UserSummaryDto;
import com.chat_application.entity.enums.FriendStatus;

import java.util.List;

public interface FriendshipService {
    public void cancelFriendRequest(Long requesterId) ;
    public FriendStatus getFriendshipStatus(Long friendId) ;
    public List<UserSummaryDto> getAllBlockedUsers() ;
    public List<UserSummaryDto> getFriendRequestSentUsers() ;
    List<FriendRequestDto> getPendingFriendRequests() ;
    FriendRequestDto sendFriendRequest(Long friendId) ;
    void acceptFriendRequest(Long requesterId) ;
    void removeFriend(Long friendId) ;
    List<UserSummaryDto> getFriends();
}
