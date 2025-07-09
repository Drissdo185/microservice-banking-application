package com.example.user_service.service;

import com.example.user_service.dto.UpdateProfileRequest;
import com.example.user_service.dto.UserProfileDto;
import com.example.user_service.dto.UserValidationResponse;

public interface UserService {
    UserProfileDto getUserProfile(Long userId);
    UserProfileDto updateProfile(Long userId, UpdateProfileRequest request);
    void deleteUser(Long userId);
    UserProfileDto getUserByUsername(String username);
    UserProfileDto getUserByEmail(String email);
    UserValidationResponse validateUser(Long userId);
}