package com.example.loan_service.dto;

import com.example.loan_service.entity.Loan;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDto {
    
    private Long id;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Loan amount is required")
    @DecimalMin(value = "0.01", message = "Loan amount must be greater than 0")
    private BigDecimal loanAmount;
    
    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.01", message = "Interest rate must be greater than 0")
    private BigDecimal interestRate;
    
    @NotNull(message = "Tenure months is required")
    @Positive(message = "Tenure months must be positive")
    private Integer tenureMonths;
    
    private BigDecimal monthlyEmi;
    private BigDecimal outstandingAmount;
    private Loan.LoanStatus status;
    private LocalDateTime createdAt;
}