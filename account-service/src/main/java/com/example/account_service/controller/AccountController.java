package com.example.account_service.controller;

import com.example.account_service.client.UserServiceClient;
import com.example.account_service.dto.*;
import com.example.account_service.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final UserServiceClient userServiceClient;

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateAccountRequest request) {
        
        Long userId = validateUserAndGetId(authHeader);
        AccountDto account = accountService.createAccount(userId, request);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AccountDto>> getUserAccounts(
            @RequestHeader("Authorization") String authHeader) {
        
        Long userId = validateUserAndGetId(authHeader);
        List<AccountDto> accounts = accountService.getUserAccounts(userId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccountById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long accountId) {
        
        Long userId = validateUserAndGetId(authHeader);
        AccountDto account = accountService.getAccountById(accountId, userId);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountDto> getAccountByNumber(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String accountNumber) {
        
        Long userId = validateUserAndGetId(authHeader);
        AccountDto account = accountService.getAccountByNumber(accountNumber, userId);
        return ResponseEntity.ok(account);
    }

    @PutMapping("/{accountId}/close")
    public ResponseEntity<AccountDto> closeAccount(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long accountId) {
        
        Long userId = validateUserAndGetId(authHeader);
        AccountDto account = accountService.closeAccount(accountId, userId);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<TransactionDto> addTransaction(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long accountId,
            @Valid @RequestBody TransactionRequest request) {
        
        Long userId = validateUserAndGetId(authHeader);
        TransactionDto transaction = accountService.addTransaction(accountId, userId, request);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<Page<TransactionDto>> getAccountTransactions(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Long userId = validateUserAndGetId(authHeader);
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionDto> transactions = accountService.getAccountTransactions(accountId, userId, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{accountId}/transactions/history")
    public ResponseEntity<List<TransactionDto>> getAccountTransactionHistory(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long accountId) {
        
        Long userId = validateUserAndGetId(authHeader);
        List<TransactionDto> transactions = accountService.getAccountTransactionHistory(accountId, userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionDto>> getUserTransactions(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Long userId = validateUserAndGetId(authHeader);
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionDto> transactions = accountService.getUserTransactions(userId, pageable);
        return ResponseEntity.ok(transactions);
    }

    private Long validateUserAndGetId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }
        
        String token = authHeader.substring(7);
        UserValidationResponse response = userServiceClient.validateUser(token);
        
        if (!response.isValid()) {
            throw new RuntimeException("Invalid or expired token");
        }
        
        return response.getUserId();
    }
}