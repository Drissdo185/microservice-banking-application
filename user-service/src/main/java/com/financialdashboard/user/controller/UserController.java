package com.financialdashboard.user.controller;

import com.financialdashboard.user.dto.UserProfileDto;
import com.financialdashboard.user.entity.User;
import com.financialdashboard.user.service.AuthService;
import com.financialdashboard.user.service.UserService;
import com.financialdashboard.user.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtUtil.getEmailFromToken(token);
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileDto profile = new UserProfileDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDto> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UserProfileDto profileDto) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        if (!authService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtUtil.getEmailFromToken(token);
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(profileDto.getName());
        // Note: Email update would require additional validation

        User updatedUser = userService.updateUser(user);

        UserProfileDto response = new UserProfileDto(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail()
        );

        return ResponseEntity.ok(response);
    }
}