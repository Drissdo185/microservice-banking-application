package com.financialdashboard.user.service.impl;


import com.financialdashboard.user.entity.User;
import com.financialdashboard.user.exception.UserAlreadyExistsException;
import com.financialdashboard.user.repository.UserRepository;
import com.financialdashboard.user.service.AuthService;
import com.financialdashboard.user.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private Set<String> blacklistedTokens = new HashSet<>();



    @Override
    public String authenticate(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Cannot find user"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Wrong password");
        }

        return jwtUtil.generateToken(user.getId(), user.getEmail());
    }

    @Override
    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }
    @Override
    public boolean validateToken(String token) {
        return !blacklistedTokens.contains(token) && jwtUtil.validateToken(token);
    }

    @Override
    public void logout(String token) {
        blacklistedTokens.add(token);
    }
    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
