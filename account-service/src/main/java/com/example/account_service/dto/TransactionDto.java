package com.example.account_service.dto;

import com.example.account_service.entity.AccountTransaction;
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
public class TransactionDto {
    
    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private AccountTransaction.TransactionType transactionType;
    private String description;
    private LocalDateTime transactionDate;
}