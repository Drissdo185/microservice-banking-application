package com.example.loan_service.client;

import com.example.loan_service.dto.UserValidationResponse;
import reactor.core.publisher.Mono;

public interface UserServiceClient {
    Mono<UserValidationResponse> validateUser(String token);
}