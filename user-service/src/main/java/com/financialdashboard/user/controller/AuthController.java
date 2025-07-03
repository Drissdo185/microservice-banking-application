package com.financialdashboard.user.controller;


import com.financialdashboard.user.dto.LoginRequest;
import com.financialdashboard.user.dto.LoginResponse;
import com.financialdashboard.user.dto.RegisterRequest;
import com.financialdashboard.user.entity.User;
import com.financialdashboard.user.service.AuthService;
import com.financialdashboard.user.service.UserService;
import com.financialdashboard.user.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setPasswordHash(registerRequest.getPassword());

        User savedUser = authService.register(user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("userId", savedUser.getId().toString());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());


        User user = authService.findUserByEmail(loginRequest.getEmail()).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                token
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Boolean> response = new HashMap<>();

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = authService.validateToken(token);
            response.put("valid", isValid);
        } else {
            response.put("valid", false);
        }

        return ResponseEntity.ok(response);
    }


}
