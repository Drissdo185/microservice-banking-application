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
    public Mono<UserValidationResponse> validateUser(String token) {
        return webClient.get()
                .uri("http://user-service/api/users/internal/validate")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    log.error("User not found during validation");
                    return Mono.error(new RuntimeException("User not found"));
                })
                .onStatus(HttpStatus.UNAUTHORIZED::equals, response -> {
                    log.error("Unauthorized token during validation");
                    return Mono.error(new RuntimeException("Unauthorized"));
                })
                .bodyToMono(UserValidationResponse.class)
                .timeout(Duration.ofSeconds(5))
                .doOnSuccess(user -> log.debug("User validation successful for ID: {}", user.getId()))
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        WebClientResponseException webClientError = (WebClientResponseException) error;
                        log.error("Error validating user: HTTP {} - {}",
                                webClientError.getStatusCode(), webClientError.getResponseBodyAsString());
                    } else {
                        log.error("Error validating user: {}", error.getMessage());
                    }
                })
                .onErrorResume(error -> {
                    log.error("User service unavailable for user validation: {}", error.getMessage());
                    return Mono.error(new RuntimeException("User service unavailable"));
                });
    }
}