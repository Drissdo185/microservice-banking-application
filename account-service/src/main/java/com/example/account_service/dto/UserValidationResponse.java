package com.example.account_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationResponse {
    
    private boolean valid;
    private Long userId;
    private String username;
    private String email;
    private String message;
}