package com.financialdashboard.user.service;

import com.financialdashboard.user.entity.User;

import java.util.Optional;

public interface AuthService {
    String authenticate(String email, String password);
    User register(User user);
    boolean validateToken(String token);
    void logout(String token);

    default Optional<User> findUserByEmail(String email) {
        return Optional.empty(); // Override in implementation
    }

}
