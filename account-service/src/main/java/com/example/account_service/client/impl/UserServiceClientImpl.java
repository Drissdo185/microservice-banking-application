package com.example.account_service.client.impl;

import com.example.account_service.client.UserServiceClient;
import com.example.account_service.dto.UserValidationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class UserServiceClientImpl implements UserServiceClient {

    private final WebClient webClient;

    public UserServiceClientImpl(WebClient.Builder webClientBuilder,
                                @Value("${user.service.url:http://user-service:8081}") String userServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(userServiceUrl).build();
    }

    @Override
    public UserValidationResponse validateUser(String token) {
        try {
            return webClient.get()
                    .uri("/api/auth/validate")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(UserValidationResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
        } catch (Exception e) {
            return new UserValidationResponse(false, null, null, null, "User validation failed: " + e.getMessage());
        }
    }
}