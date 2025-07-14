package com.example.account_service.client;

import com.example.account_service.dto.UserValidationResponse;

public interface UserServiceClient {
    UserValidationResponse validateUser(String token);
}