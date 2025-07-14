package com.example.account_service.dto;

import com.example.account_service.entity.Account;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    
    @NotNull(message = "Account type is required")
    private Account.AccountType accountType;
    
    private String description;
}