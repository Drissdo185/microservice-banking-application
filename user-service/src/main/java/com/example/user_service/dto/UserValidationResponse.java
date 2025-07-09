package com.example.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationResponse {
    private Long id;
    private String username;
    private String email;
    private boolean isActive;
    
    public UserValidationResponse(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.isActive = true;
    }
}