package com.chat_application.services;

import com.chat_application.dto.FriendRequestDto;
import com.chat_application.dto.UserSummaryDto;
import com.chat_application.entity.Friendship;
import com.chat_application.entity.User;
import com.chat_application.entity.enums.FriendStatus;
import com.chat_application.exception.ResourceNotFoundException;
import com.chat_application.exception.UnAuthorisedException;
import com.chat_application.repositories.FriendshipRepository;
import com.chat_application.repositories.MessageRepository;
import com.chat_application.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.chat_application.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendshipServiceImpl implements FriendshipService{

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final FriendshipRepository friendshipRepository;

    @Override
    @Transactional
    public FriendRequestDto sendFriendRequest(Long friendId) {
        User user = getCurrentUser();
        log.debug("User {} sending friend request to user {}", user.getId(), friendId);

        validateUserIds(user.getId(), friendId);

        User friend = findUserById(friendId);

        Optional<Friendship> existingFriendship = friendshipRepository.findByUser1AndUser2(user, friend)
                .or(() -> friendshipRepository.findByUser1AndUser2(friend, user));

        if (existingFriendship.isPresent()) {
            FriendStatus status = existingFriendship.get().getStatus();
            throw new ResourceNotFoundException(
                    "Friendship already exists with status: " + status);
        }

        Friendship friendship = Friendship.builder()
                .user1(user)
                .user2(friend)
                .status(FriendStatus.PENDING)
                .requestedBy(user)
                .requestedAt(LocalDateTime.now())
                .build();

        Friendship savedFriendship = friendshipRepository.save(friendship);
        log.info("Friend request sent from user {} to user {}", user.getId(), friendId);

        return FriendRequestDto.builder().user2(savedFriendship.getUser2().getId()).build();
    }

    @Override
    @Transactional
    public void acceptFriendRequest(Long requesterId) {
        User user = getCurrentUser();
        log.debug("User {} accepting friend request from user {}", user.getId(), requesterId);


        User requester = findUserById(requesterId);

        Friendship friendship = friendshipRepository.findByUser1AndUser2(requester, user)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));

        if (friendship.getStatus() != FriendStatus.PENDING) {
            throw new UnAuthorisedException("Friend request is not in pending state");
        }

        friendship.setStatus(FriendStatus.ACCEPTED);
        friendship.setAcceptedAt(LocalDateTime.now());

        friendshipRepository.save(friendship);
        log.info("User {} accepted friend request from user {}", user, requesterId);
    }

    @Override
    @Transactional
    public void removeFriend(Long friendId) {
        User user = getCurrentUser();
        log.debug("User {} removing friend {}", user.getId(), friendId);

        validateUserIds(user.getId(), friendId);

        User friend = findUserById(friendId);

        Friendship friendship = findFriendshipBetweenUsers(user, friend);

        friendshipRepository.delete(friendship);
        log.info("User {} successfully removed friend {}", user.getId(), friendId);
    }

    @Override
    public List<FriendRequestDto> getPendingFriendRequests() {
        User user = getCurrentUser();

        List<Friendship> pendingRequests = friendshipRepository.findByUser2AndStatus(user, FriendStatus.PENDING);

        return pendingRequests.stream()
                .map(friendship -> modelMapper.map(friendship, FriendRequestDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserSummaryDto> getFriends() {
        User user = getCurrentUser() ;

        List<Friendship> friendships = friendshipRepository.findByUser1OrUser2AndStatus(user, FriendStatus.ACCEPTED);

        return friendships.stream()
                .map(friendship -> {
                    User friend = friendship.getUser1().equals(user) ?
                            friendship.getUser2() : friendship.getUser1();
                    return modelMapper.map(friend, UserSummaryDto.class);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void cancelFriendRequest(Long friendId) {
        User user = getCurrentUser();
        log.info("Attempting to cancel friend request from user {} to user {}", user.getId(), friendId);
        validateUserIds(user.getId(), friendId);

        User friend = findUserById(friendId);

        Friendship friendship = findFriendshipBetweenUsers(user,friend) ;

        if (friendship.getStatus() != FriendStatus.PENDING) {
            log.warn("Friend request from user {} to user {} is not pending", user.getId(), friendId);
            throw new ResourceNotFoundException("No pending friend request found");
        }

        friendshipRepository.delete(friendship);
        log.info("Friend request from user {} to user {} successfully cancelled", user.getId(), friendId);
    }

    @Override
    public FriendStatus getFriendshipStatus(Long friendId) {
        User user = getCurrentUser() ;
        validateUserIds(user.getId(),friendId);
        User friend = findUserById(friendId) ;
        Friendship friendship = findFriendshipBetweenUsers(user,friend) ;
        return friendship.getStatus();
    }

    @Override
    public List<UserSummaryDto> getAllBlockedUsers() {
        User user = getCurrentUser();
        log.info("Fetching all blocked users for user {}", user.getId());


        List<Friendship> friends = friendshipRepository.findByUser1AndStatus(user, FriendStatus.BLOCKED);
        List<UserSummaryDto> blockedUsers = friends.stream()
                .map(friend -> modelMapper.map(friend.getUser2(), UserSummaryDto.class))
                .collect(Collectors.toList());

        log.info("User {} has blocked {} users", user.getId(), blockedUsers.size());
        return blockedUsers;
    }
    @Override
    public List<UserSummaryDto> getFriendRequestSentUsers() {
        User user = getCurrentUser();
        log.info("Fetching sent friend requests for user {}", user.getId());

        List<Friendship> friends = friendshipRepository.findByUser1AndStatus(user, FriendStatus.PENDING);
        List<UserSummaryDto> sentRequests = friends.stream()
                .map(friend -> modelMapper.map(friend.getUser2(), UserSummaryDto.class))
                .collect(Collectors.toList());

        log.info("User {} has sent {} friend requests", user.getId(), sentRequests.size());
        return sentRequests;
    }

    private void validateUserIds(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ResourceNotFoundException("User cannot perform this action on themselves");
        }
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Friendship findFriendshipBetweenUsers(User user1, User user2) {
        return friendshipRepository.findBidirectionalByUserIds(user1.getId(),user2.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Friendship does not exist between: " + user1.getName() + " and " + user2.getName()));
    }
}
