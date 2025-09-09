package com.chat_application.services;

import com.chat_application.dto.*;
import com.chat_application.entity.Friendship;
import com.chat_application.entity.Message;
import com.chat_application.entity.User;
import com.chat_application.entity.enums.FriendStatus;
import com.chat_application.entity.enums.OnlineStatus;
import com.chat_application.entity.enums.UserRole;
import com.chat_application.entity.enums.UserStatus;
import com.chat_application.exception.ResourceNotFoundException;
import com.chat_application.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.chat_application.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService , UserDetailsService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final FriendshipRepository friendshipRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional
    public UserDto updateUser(UserUpdateRequestDto userUpdateRequestDto) {
        User user = getCurrentUser();
        log.debug("Updating user with id: {}", user.getId());

        modelMapper.map(userUpdateRequestDto, user);

        User updatedUser = userRepository.save(user);
        log.info("Successfully updated user with id: {}", user.getId());

        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Transactional
    @Override
    public void deleteUser() {

        User user = getCurrentUser();
        log.debug("Deleting user with id: {}", user.getId());

        List<Message> userMessages = messageRepository.findBySender(user);
        userMessages.forEach(message -> message.setSender(null));
        messageRepository.saveAll(userMessages);

        friendshipRepository.deleteByUser1OrUser2(user);

        userRepository.delete(user);
        log.info("Successfully deleted user with id: {}", user.getId());
    }

    @Override
    @Transactional
    public void deactivateUser() {

        User user = getCurrentUser();
        log.debug("Deactivating user with id: {}", user.getId());

        user.setStatus(UserStatus.DELETED);
        user.setOnlineStatus(OnlineStatus.OFFLINE);

        userRepository.save(user);
        log.info("Successfully deactivated user with id: {}", user.getId());
    }

    @Override
    @Transactional
    public void activateUser() {

        User user = getCurrentUser();
        log.debug("Activating user with id: {}", user.getId());
        if (user.getStatus() != UserStatus.DELETED) {
            throw new IllegalStateException("User is not in deactivated state");
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        log.info("Successfully activated user with id: {}", user.getId());
    }

    @Override
    @Transactional
    public void blockUser(Long friendId) {

        User user = getCurrentUser() ;
        log.debug("User {} blocking user {}", user.getId(), friendId);
        validateUserIds(user.getId(), friendId);
        User friend = findUserById(friendId);

        Friendship friendship = findFriendshipBetweenUsers(user, friend);

        friendship.setStatus(FriendStatus.BLOCKED);
        friendship.setRequestedBy(user);
        friendship.setRequestedAt(LocalDateTime.now());

        friendshipRepository.save(friendship);
        log.info("User {} successfully blocked user {}", user.getId(), friendId);
    }

    @Override
    @Transactional
    public void unblockUser(Long friendId) {
        User user = getCurrentUser();
        log.debug("User {} unblocking user {}", user.getId(), friendId);

        validateUserIds(user.getId(), friendId);

        User friend = findUserById(friendId);

        Friendship friendship = findFriendshipBetweenUsers(user, friend);

        if (friendship.getStatus() != FriendStatus.BLOCKED) {
            throw new IllegalStateException("Users are not in blocked state");
        }

        friendship.setStatus(FriendStatus.ACCEPTED);
        friendship.setRequestedBy(user);
        friendship.setRequestedAt(LocalDateTime.now());

        friendshipRepository.save(friendship);
        log.info("User {} successfully unblocked user {}", user.getId(), friendId);
    }

    @Override
    @Transactional
    public void updateOnlineStatus() {
        User user = getCurrentUser();
        OnlineStatus newStatus = user.getOnlineStatus() == OnlineStatus.OFFLINE ?
                OnlineStatus.ONLINE : OnlineStatus.OFFLINE;

        user.setOnlineStatus(newStatus);

        if (newStatus == OnlineStatus.OFFLINE) {
            user.setLastSeen(LocalDateTime.now());
        }

        userRepository.save(user);
        log.debug("Updated online status for user with id {} to {}", user.getId(), newStatus);
    }

    @Override
    public LocalDateTime getLastSeen() {
        return getCurrentUser().getLastSeen();
    }

    @Override
    @Transactional
    public void updateLastSeen() {
        User user = getCurrentUser();
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public List<UserSummaryDto> searchUser(String name) {
        log.debug("Searching users with name containing: {}", name);

        List<User> users = userRepository.findUserByNameContainingIgnoreCase(name);

        return users.stream()
                .map(user -> modelMapper.map(user, UserSummaryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> getAllUsers() {
        User user = getCurrentUser() ;
        if(!user.getRoles().contains(UserRole.ADMIN)){
            throw new ResourceNotFoundException("User is not ADMIN") ;
        }
        List<User> l = userRepository.findAll() ;
        return l.stream().map((x) ->
            modelMapper.map(x,UserDto.class)
        ).collect(Collectors.toList()) ;
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+userId));
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

    private void validateUserIds(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ResourceNotFoundException("User cannot perform this action on themselves");
        }
    }
}

