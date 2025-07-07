package com.example.user_service.service;

import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.LoginResponse;
import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.dto.UserProfileDto;

public interface AuthService {
    UserProfileDto register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    void logout(String token);
    boolean validateToken(String token);
    Long getUserIdFromToken(String token);
}