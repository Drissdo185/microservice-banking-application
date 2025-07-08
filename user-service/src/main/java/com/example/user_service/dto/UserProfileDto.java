package com.example.user_service.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
}