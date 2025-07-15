package com.example.loan_service.service;

import com.example.loan_service.dto.LoanDto;
import com.example.loan_service.dto.LoanPaymentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface LoanService {
    
    LoanDto createLoan(LoanDto loanDto, String token);
    
    LoanDto getLoanById(Long id);
    
    List<LoanDto> getLoansByUserId(Long userId);
    
    Page<LoanDto> getAllLoans(Pageable pageable);
    
    LoanDto updateLoan(Long id, LoanDto loanDto);
    
    void deleteLoan(Long id);
    
    LoanDto updateLoanStatus(Long id, String status);
    
    LoanPaymentDto makePayment(Long loanId, BigDecimal amount);
    
    List<LoanPaymentDto> getPaymentsByLoanId(Long loanId);
    
    BigDecimal calculateEmi(BigDecimal loanAmount, BigDecimal interestRate, Integer tenureMonths);
    
    List<LoanDto> getActiveLoansByUserId(Long userId);
    
    boolean hasEligibleCreditScore(Long userId);
}