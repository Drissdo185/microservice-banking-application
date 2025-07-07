package com.example.user_service.controller;

import com.example.user_service.dto.UpdateProfileRequest;
import com.example.user_service.dto.UserProfileDto;
import com.example.user_service.service.AuthService;
import com.example.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final AuthService authService;
    
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile(Authentication authentication) {
        String username = authentication.getName();
        UserProfileDto profile = userService.getUserByUsername(username);
        return ResponseEntity.ok(profile);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @RequestHeader("Authorization") String token) {
        
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = authService.getUserIdFromToken(token);
        UserProfileDto updatedProfile = userService.updateProfile(userId, request);
        return ResponseEntity.ok(updatedProfile);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDto> getUserById(@PathVariable Long userId) {
        UserProfileDto profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }
    
    @GetMapping("/username/{username}")
    public ResponseEntity<UserProfileDto> getUserByUsername(@PathVariable String username) {
        UserProfileDto profile = userService.getUserByUsername(username);
        return ResponseEntity.ok(profile);
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<UserProfileDto> getUserByEmail(@PathVariable String email) {
        UserProfileDto profile = userService.getUserByEmail(email);
        return ResponseEntity.ok(profile);
    }
    
    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteProfile(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = authService.getUserIdFromToken(token);
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }
}