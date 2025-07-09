package com.example.card_service.client.impl;

import com.example.card_service.client.UserServiceClient;
import com.example.card_service.dto.UserValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClientImpl implements UserServiceClient {

    private final WebClient webClient;
    
    @Value("${user-service.url}")
    private String userServiceUrl;

    @Override
    public Mono<UserValidationResponse> validateUser(Long userId, String token) {
        log.debug("Validating user with ID: {} using User Service", userId);
        
        return webClient.get()
                .uri(userServiceUrl + "/api/users/internal/validate/" + userId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    log.warn("User with ID {} not found", userId);
                    return Mono.error(new RuntimeException("User not found"));
                })
                .onStatus(HttpStatus.UNAUTHORIZED::equals, response -> {
                    log.warn("Unauthorized access when validating user {}", userId);
                    return Mono.error(new RuntimeException("Unauthorized"));
                })
                .bodyToMono(UserValidationResponse.class)
                .timeout(Duration.ofSeconds(5))
                .doOnSuccess(user -> log.debug("User validation successful for ID: {}", userId))
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        WebClientResponseException webClientError = (WebClientResponseException) error;
                        log.error("Error validating user {}: HTTP {} - {}", 
                                userId, webClientError.getStatusCode(), webClientError.getResponseBodyAsString());
                    } else {
                        log.error("Error validating user {}: {}", userId, error.getMessage());
                    }
                })
                .onErrorResume(error -> {
                    log.error("User service unavailable for user validation: {}", error.getMessage());
                    return Mono.error(new RuntimeException("User service unavailable"));
                });
    }
}