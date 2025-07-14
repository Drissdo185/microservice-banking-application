package com.example.account_service.dto;

import com.example.account_service.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    
    private Long id;
    private Long userId;
    private String accountNumber;
    private Account.AccountType accountType;
    private BigDecimal balance;
    private Account.AccountStatus status;
    private LocalDateTime createdAt;
}