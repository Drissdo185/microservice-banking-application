package com.example.user_service.service.impl;

import com.example.user_service.dto.SessionMetadata;
import com.example.user_service.service.SessionStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisSessionStore implements SessionStore {
    
    private static final String SESSION_PREFIX = "session:";
    private static final String USER_SESSIONS_PREFIX = "user_sessions:";
    private static final Duration DEFAULT_SESSION_TIMEOUT = Duration.ofHours(24);
    
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    
    @Override
    public void createSession(String token, SessionMetadata metadata) {
        try {
            String sessionKey = SESSION_PREFIX + token;
            String userSessionsKey = USER_SESSIONS_PREFIX + metadata.getUserId();
            
            String metadataJson = objectMapper.writeValueAsString(metadata);
            
            redisTemplate.opsForValue().set(sessionKey, metadataJson, DEFAULT_SESSION_TIMEOUT);
            redisTemplate.opsForSet().add(userSessionsKey, token);
            redisTemplate.expire(userSessionsKey, DEFAULT_SESSION_TIMEOUT);
            
            log.debug("Created session for user {} with token {}", metadata.getUserId(), token.substring(0, 10) + "...");
        } catch (JsonProcessingException e) {
            log.error("Error serializing session metadata", e);
            throw new RuntimeException("Failed to create session", e);
        }
    }
    
    @Override
    public Optional<SessionMetadata> getSession(String token) {
        try {
            String sessionKey = SESSION_PREFIX + token;
            String metadataJson = redisTemplate.opsForValue().get(sessionKey);
            
            if (metadataJson == null) {
                return Optional.empty();
            }
            
            SessionMetadata metadata = objectMapper.readValue(metadataJson, SessionMetadata.class);
            
            if (metadata.getExpiresAt().isBefore(LocalDateTime.now())) {
                invalidateSession(token);
                return Optional.empty();
            }
            
            return Optional.of(metadata);
        } catch (JsonProcessingException e) {
            log.error("Error deserializing session metadata for token {}", token.substring(0, 10) + "...", e);
            return Optional.empty();
        }
    }
    
    @Override
    public void updateLastActivity(String token) {
        Optional<SessionMetadata> sessionOpt = getSession(token);
        if (sessionOpt.isPresent()) {
            SessionMetadata metadata = sessionOpt.get();
            metadata.setLastActivity(LocalDateTime.now());
            
            try {
                String sessionKey = SESSION_PREFIX + token;
                String metadataJson = objectMapper.writeValueAsString(metadata);
                redisTemplate.opsForValue().set(sessionKey, metadataJson, DEFAULT_SESSION_TIMEOUT);
            } catch (JsonProcessingException e) {
                log.error("Error updating last activity for session", e);
            }
        }
    }
    
    @Override
    public void invalidateSession(String token) {
        Optional<SessionMetadata> sessionOpt = getSession(token);
        if (sessionOpt.isPresent()) {
            SessionMetadata metadata = sessionOpt.get();
            String sessionKey = SESSION_PREFIX + token;
            String userSessionsKey = USER_SESSIONS_PREFIX + metadata.getUserId();
            
            redisTemplate.delete(sessionKey);
            redisTemplate.opsForSet().remove(userSessionsKey, token);
            
            log.debug("Invalidated session for user {} with token {}", 
                     metadata.getUserId(), token.substring(0, 10) + "...");
        }
    }
    
    @Override
    public void invalidateAllUserSessions(Long userId) {
        String userSessionsKey = USER_SESSIONS_PREFIX + userId;
        Set<String> tokens = redisTemplate.opsForSet().members(userSessionsKey);
        
        if (tokens != null && !tokens.isEmpty()) {
            List<String> sessionKeys = tokens.stream()
                    .map(token -> SESSION_PREFIX + token)
                    .collect(Collectors.toList());
            
            redisTemplate.delete(sessionKeys);
            redisTemplate.delete(userSessionsKey);
            
            log.debug("Invalidated {} sessions for user {}", tokens.size(), userId);
        }
    }
    
    @Override
    public List<SessionMetadata> getUserActiveSessions(Long userId) {
        String userSessionsKey = USER_SESSIONS_PREFIX + userId;
        Set<String> tokens = redisTemplate.opsForSet().members(userSessionsKey);
        
        if (tokens == null || tokens.isEmpty()) {
            return List.of();
        }
        
        return tokens.stream()
                .map(this::getSession)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(metadata -> metadata.getExpiresAt().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isSessionValid(String token) {
        return getSession(token).isPresent();
    }
    
    @Override
    public void cleanupExpiredSessions() {
        log.info("Redis TTL handles automatic cleanup of expired sessions");
    }
    
    @Override
    public int getActiveSessionCount(Long userId) {
        return getUserActiveSessions(userId).size();
    }
}