package com.chat_application.services;

import com.chat_application.dto.LoginDto;
import com.chat_application.dto.SignUpDto;
import com.chat_application.dto.UserDto;
import com.chat_application.entity.User;
import com.chat_application.entity.enums.OnlineStatus;
import com.chat_application.entity.enums.UserRole;
import com.chat_application.entity.enums.UserStatus;
import com.chat_application.exception.ResourceNotFoundException;
import com.chat_application.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final ModelMapper modelMapper ;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder ;

    @Override
    @Transactional
    public UserDto signUp(SignUpDto signUpDto) {
        log.debug("Creating user with email: {}", signUpDto.getEmail());

        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            throw new ResourceNotFoundException("User already exists with email: " + signUpDto.getEmail());
        }

        User user = modelMapper.map(signUpDto, User.class);
        user.setOnlineStatus(OnlineStatus.OFFLINE);
        user.setLastSeen(LocalDateTime.now());
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(Set.of(UserRole.GENERAL));
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("Successfully created user with ID: {} and email: {}", savedUser.getId(), savedUser.getEmail());

        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public String refreshToken(String refreshToken) {
        Long id = jwtService.getUserIdFromToken(refreshToken) ;
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: "+id)) ;
        return jwtService.generateAccessToken(user);
    }

    @Override
    public String[] login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword())
        );
        User user = (User)authentication.getPrincipal() ;
        String[] a = new String[2] ;
        a[0] = jwtService.generateAccessToken(user) ;
        a[1] = jwtService.generateRefreshToken(user) ;
        return a;
    }
}
