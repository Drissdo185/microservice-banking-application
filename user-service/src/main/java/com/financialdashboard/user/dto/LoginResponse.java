package com.financialdashboard.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;


@Data
@AllArgsConstructor
public class LoginResponse {

    private UUID userId;
    private String name;
    private String email;
    private String token;

}
