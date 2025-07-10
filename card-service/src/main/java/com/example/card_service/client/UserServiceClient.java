package com.example.card_service.client;

import com.example.card_service.dto.UserValidationResponse;
import reactor.core.publisher.Mono;

public interface UserServiceClient {
    Mono<UserValidationResponse> validateUser(String token);
}