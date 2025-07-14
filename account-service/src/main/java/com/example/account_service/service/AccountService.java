package com.example.account_service.service;

import com.example.account_service.dto.AccountDto;
import com.example.account_service.dto.CreateAccountRequest;
import com.example.account_service.dto.TransactionDto;
import com.example.account_service.dto.TransactionRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {
    
    AccountDto createAccount(Long userId, CreateAccountRequest request);
    
    List<AccountDto> getUserAccounts(Long userId);
    
    AccountDto getAccountById(Long accountId, Long userId);
    
    AccountDto getAccountByNumber(String accountNumber, Long userId);
    
    AccountDto closeAccount(Long accountId, Long userId);
    
    TransactionDto addTransaction(Long accountId, Long userId, TransactionRequest request);
    
    Page<TransactionDto> getAccountTransactions(Long accountId, Long userId, Pageable pageable);
    
    List<TransactionDto> getAccountTransactionHistory(Long accountId, Long userId);
    
    Page<TransactionDto> getUserTransactions(Long userId, Pageable pageable);
}