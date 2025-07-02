package com.financialdashboard.user.service;

import com.financialdashboard.user.entity.User;

public interface AuthService {
    String authenticate(String username, String password);
    User register(User user);
    boolean validateToken(String token);
    void logout(String token);

}
