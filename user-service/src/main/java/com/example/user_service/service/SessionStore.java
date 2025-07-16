package com.example.user_service.service;

import com.example.user_service.dto.SessionMetadata;

import java.util.List;
import java.util.Optional;

public interface SessionStore {
    
    void createSession(String token, SessionMetadata metadata);
    
    Optional<SessionMetadata> getSession(String token);
    
    void updateLastActivity(String token);
    
    void invalidateSession(String token);
    
    void invalidateAllUserSessions(Long userId);
    
    List<SessionMetadata> getUserActiveSessions(Long userId);
    
    boolean isSessionValid(String token);
    
    void cleanupExpiredSessions();
    
    int getActiveSessionCount(Long userId);
}