package com.financialdashboard.user.service;

import com.financialdashboard.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(User user);
    Optional<User> findUserByEmail(String email);
    List<User> findAllUsers();
    User updateUser(User user);
    void deleteUser(User user);







}
