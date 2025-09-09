package com.chat_application.controllers;

import com.chat_application.dto.FriendRequestDto;
import com.chat_application.dto.UserSummaryDto;
import com.chat_application.entity.enums.FriendStatus;
import com.chat_application.services.FriendshipService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService ;

    @GetMapping("/getAllFriends")
    public ResponseEntity<List<UserSummaryDto>> getAllFriends(){
        return new ResponseEntity<>(friendshipService.getFriends(),HttpStatus.FOUND) ;
    }

    @GetMapping("/getAllPendingRequests")
    public ResponseEntity<List<FriendRequestDto>> getAllPendingRequests(){
        return new ResponseEntity<>(friendshipService.getPendingFriendRequests(),HttpStatus.FOUND) ;
    }

    @PostMapping("/sendFriendRequest/{friendId}")
    public ResponseEntity<FriendRequestDto> sendFriendRequest(@PathVariable @Positive Long friendId){
        return new ResponseEntity<>(friendshipService.sendFriendRequest(friendId),HttpStatus.CREATED) ;
    }

    @PatchMapping("/acceptFriendRequest/{requestedId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable @Positive Long requestedId){
        friendshipService.acceptFriendRequest(requestedId) ;
        return ResponseEntity.noContent().build() ;
    }

    @DeleteMapping("/removeFriend/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable @Positive Long friendId){
        friendshipService.removeFriend(friendId);
        return ResponseEntity.noContent().build() ;
    }

    @PatchMapping("/cancelRequest/{requesterId}")
    public ResponseEntity<Void> cancelRequest(@PathVariable Long requesterId){
        friendshipService.cancelFriendRequest(requesterId);
        return ResponseEntity.noContent().build() ;
    }

    @GetMapping("/getFriendshipStatus/{friendId}")
    public ResponseEntity<FriendStatus> getFriendStatus(@PathVariable Long friendId){
        return ResponseEntity.status(HttpStatus.FOUND).body(friendshipService.getFriendshipStatus(friendId)) ;
    }

    @GetMapping("/getAllBlockedUsers")
    public ResponseEntity<List<UserSummaryDto>> getAllBlockedUsers(){
        return new ResponseEntity<>(friendshipService.getAllBlockedUsers(),HttpStatus.FOUND) ;
    }

    @GetMapping("/getFriendRequestSentUsers")
    public ResponseEntity<List<UserSummaryDto>> getFriendRequestSentUsers(){
        return new ResponseEntity<>(friendshipService.getFriendRequestSentUsers(),HttpStatus.FOUND) ;
    }
}
