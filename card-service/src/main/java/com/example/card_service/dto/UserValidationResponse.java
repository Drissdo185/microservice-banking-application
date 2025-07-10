package com.example.card_service.dto;

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


    private String firstName;
    private String lastName;
    private String phone;


}