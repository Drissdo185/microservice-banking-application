package com.example.user_service.service.impl;

import com.example.user_service.dto.UpdateProfileRequest;
import com.example.user_service.dto.UserProfileDto;
import com.example.user_service.dto.UserValidationResponse;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserProfile;
import com.example.user_service.repository.UserProfileRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.repository.UserSessionRepository;
import com.example.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserSessionRepository userSessionRepository;
    
    @Override
    @Cacheable(value = "userProfiles", key = "#userId")
    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return mapToUserProfileDto(user);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = {"userProfiles", "usersByUsername", "usersByEmail", "userValidations"}, key = "#userId")
    public UserProfileDto updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(user);
            user.setProfile(profile);
        }
        
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhone(request.getPhone());
        
        User savedUser = userRepository.save(user);
        return mapToUserProfileDto(savedUser);
    }
    
    @Override
    @Transactional
    @CacheEvict(value = {"userProfiles", "usersByUsername", "usersByEmail", "userValidations"}, key = "#userId")
    public void deleteUser(Long userId) {
        userSessionRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }
    
    @Override
    @Cacheable(value = "usersByUsername", key = "#username")
    public UserProfileDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return mapToUserProfileDto(user);
    }
    
    @Override
    @Cacheable(value = "usersByEmail", key = "#email")
    public UserProfileDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return mapToUserProfileDto(user);
    }
    
    @Override
    @Cacheable(value = "userValidations", key = "#userId")
    public UserValidationResponse validateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return new UserValidationResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            true
        );
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

        }
        
        return dto;
    }
}