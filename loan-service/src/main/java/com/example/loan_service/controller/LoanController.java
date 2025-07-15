package com.example.loan_service.controller;

import com.example.loan_service.dto.LoanDto;
import com.example.loan_service.dto.LoanPaymentDto;
import com.example.loan_service.service.LoanService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanDto> createLoan(@Valid @RequestBody LoanDto loanDto, 
                                             HttpServletRequest request,
                                             @RequestHeader("Authorization") String token) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        String bearerToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        LoanDto createdLoan = loanService.createLoan(loanDto, bearerToken);
        return new ResponseEntity<>(createdLoan, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanDto> getLoanById(@PathVariable Long id) {
        LoanDto loan = loanService.getLoanById(id);
        return ResponseEntity.ok(loan);
    }

    @GetMapping("/user")
    public ResponseEntity<List<LoanDto>> getLoansByUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        List<LoanDto> loans = loanService.getLoansByUserId(userId);
        return ResponseEntity.ok(loans);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanDto>> getLoansByUserIdAdmin(@PathVariable Long userId) {
        List<LoanDto> loans = loanService.getLoansByUserId(userId);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/user/active")
    public ResponseEntity<List<LoanDto>> getActiveLoansByUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        List<LoanDto> loans = loanService.getActiveLoansByUserId(userId);
        return ResponseEntity.ok(loans);
    }

    @GetMapping
    public ResponseEntity<Page<LoanDto>> getAllLoans(Pageable pageable) {
        Page<LoanDto> loans = loanService.getAllLoans(pageable);
        return ResponseEntity.ok(loans);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoanDto> updateLoan(@PathVariable Long id, @Valid @RequestBody LoanDto loanDto) {
        LoanDto updatedLoan = loanService.updateLoan(id, loanDto);
        return ResponseEntity.ok(updatedLoan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<LoanDto> updateLoanStatus(@PathVariable Long id, @RequestParam String status) {
        LoanDto updatedLoan = loanService.updateLoanStatus(id, status);
        return ResponseEntity.ok(updatedLoan);
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<LoanPaymentDto> makePayment(@PathVariable Long id, @RequestParam BigDecimal amount) {
        LoanPaymentDto payment = loanService.makePayment(id, amount);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/payments")
    public ResponseEntity<List<LoanPaymentDto>> getPaymentsByLoanId(@PathVariable Long id) {
        List<LoanPaymentDto> payments = loanService.getPaymentsByLoanId(id);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/calculate-emi")
    public ResponseEntity<BigDecimal> calculateEmi(@RequestParam BigDecimal loanAmount,
                                                  @RequestParam BigDecimal interestRate,
                                                  @RequestParam Integer tenureMonths) {
        BigDecimal emi = loanService.calculateEmi(loanAmount, interestRate, tenureMonths);
        return ResponseEntity.ok(emi);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Loan Service is running");
    }
}