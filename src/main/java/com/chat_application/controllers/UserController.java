package com.chat_application.controllers;

import com.chat_application.dto.*;
import com.chat_application.services.AuthService;
import com.chat_application.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService ;
    private final AuthService authService ;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody @Valid SignUpDto user){
        return new ResponseEntity<>(authService.signUp(user), HttpStatus.CREATED) ;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto , HttpServletRequest request , HttpServletResponse response){
        String[] tokens = authService.login(loginDto) ;
        Cookie cookie =  new Cookie("refreshToken",tokens[1]) ;
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response){
        SecurityContextHolder.clearContext();
        Cookie refreshCookie = new Cookie("refreshToken",null) ;
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto){
        return new ResponseEntity<>(userService.updateUser(userUpdateRequestDto),HttpStatus.OK) ;
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest httpServletRequest){
        String refreshToken =   Arrays.stream(httpServletRequest.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found inside the cookies")) ;
        String accessToken = authService.refreshToken(refreshToken) ;
        return ResponseEntity.ok(new LoginResponseDto(accessToken)) ;
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(){
        userService.deleteUser();
        return ResponseEntity.noContent().build() ;
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<?> deactivateUser(){
        userService.deactivateUser();
        return ResponseEntity.noContent().build() ;
    }

    @PatchMapping("/activate")
    public ResponseEntity<?> activateUser(){
        userService.activateUser();
        return ResponseEntity.noContent().build() ;
    }

    @PatchMapping("/updateOnlineStatus")
    public ResponseEntity<?> updateOnlineStatus(){
        userService.updateOnlineStatus();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getLastSeen")
    public ResponseEntity<LocalDateTime> getLastSeen(){
        return ResponseEntity.ok(userService.getLastSeen()) ;
    }

    @PatchMapping("/updateLastSeen")
    public ResponseEntity<?> updateLastSeen(){
        userService.updateLastSeen();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/searchUser/{name}")
    public ResponseEntity<List<UserSummaryDto>> searchUser(@PathVariable @NotBlank @Size(min = 2, max = 50) String name){
        return new ResponseEntity<>(userService.searchUser(name),HttpStatus.FOUND) ;
    }

    @PatchMapping("/blockUser/{friendId}")
    public ResponseEntity<?> blockUser(@PathVariable @Positive Long friendId){
        userService.blockUser(friendId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/unblockUser/{friendId}")
    public ResponseEntity<?> unblockUser(@PathVariable @Positive Long friendId){
        userService.unblockUser(friendId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        return new ResponseEntity<>(userService.getAllUsers(),HttpStatus.FOUND) ;
    }

}
