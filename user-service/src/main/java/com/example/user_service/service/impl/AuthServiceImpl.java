package com.example.user_service.service.impl;

import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.LoginResponse;
import com.example.user_service.dto.RegisterRequest;
import com.example.user_service.dto.UserProfileDto;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserProfile;
import com.example.user_service.entity.UserSession;
import com.example.user_service.exception.UserAlreadyExistsException;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.repository.UserSessionRepository;
import com.example.user_service.service.AuthService;
import com.example.user_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    @Transactional
    public UserProfileDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("User with username " + request.getUsername() + " already exists");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        User savedUser = userRepository.save(user);
        
        UserProfile profile = new UserProfile();
        profile.setUser(savedUser);
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhone(request.getPhone());
        
        savedUser.setProfile(profile);
        userRepository.save(savedUser);
        
        return mapToUserProfileDto(savedUser);
    }
    
    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmailOrUsername())
                .or(() -> userRepository.findByUsername(request.getEmailOrUsername()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        
        UserSession session = new UserSession();
        session.setUser(user);
        session.setToken(token);
        session.setExpiresAt(LocalDateTime.now().plusSeconds(86400));
        userSessionRepository.save(session);
        
        return new LoginResponse(token, user.getId(), user.getUsername(), user.getEmail());
    }
    
    @Override
    @Transactional
    public void logout(String token) {
        userSessionRepository.findByToken(token)
                .ifPresent(userSessionRepository::delete);
    }
    
    @Override
    public boolean validateToken(String token) {
        Optional<UserSession> session = userSessionRepository.findByToken(token);
        
        if (session.isEmpty() || session.get().isExpired()) {
            return false;
        }
        
        return jwtUtil.isValidToken(token);
    }
    
    @Override
    public Long getUserIdFromToken(String token) {
        return jwtUtil.extractUserId(token);
    }
    
    private UserProfileDto mapToUserProfileDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        
        if (user.getProfile() != null) {
            dto.setFirstName(user.getProfile().getFirstName());
            dto.setLastName(user.getProfile().getLastName());
            dto.setPhone(user.getProfile().getPhone());
            dto.setAvatarUrl(user.getProfile().getAvatarUrl());
            dto.setPreferences(user.getProfile().getPreferences());
        }
        
        return dto;
    }
}