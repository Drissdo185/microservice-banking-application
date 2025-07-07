package com.example.user_service.repository;

import com.example.user_service.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    
    Optional<UserSession> findByToken(String token);
    
    List<UserSession> findByUserId(Long userId);
    
    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.expiresAt < :now")
    void deleteExpiredSessions(LocalDateTime now);
    
    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.user.id = :userId")
    void deleteByUserId(Long userId);
}