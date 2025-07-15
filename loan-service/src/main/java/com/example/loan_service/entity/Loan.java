package com.example.loan_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "loan_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal loanAmount;
    
    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;
    
    @Column(name = "tenure_months", nullable = false)
    private Integer tenureMonths;
    
    @Column(name = "monthly_emi", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyEmi;
    
    @Column(name = "outstanding_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal outstandingAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private LoanStatus status = LoanStatus.ACTIVE;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum LoanStatus {
        ACTIVE, PAID, DEFAULT
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}