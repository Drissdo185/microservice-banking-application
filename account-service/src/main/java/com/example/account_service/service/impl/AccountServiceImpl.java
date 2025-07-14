package com.example.account_service.service.impl;

import com.example.account_service.dto.AccountDto;
import com.example.account_service.dto.CreateAccountRequest;
import com.example.account_service.dto.TransactionDto;
import com.example.account_service.dto.TransactionRequest;
import com.example.account_service.entity.Account;
import com.example.account_service.entity.AccountTransaction;
import com.example.account_service.exception.AccountAlreadyExistsException;
import com.example.account_service.exception.AccountNotFoundException;
import com.example.account_service.exception.InsufficientBalanceException;
import com.example.account_service.repository.AccountRepository;
import com.example.account_service.repository.AccountTransactionRepository;
import com.example.account_service.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository transactionRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public AccountDto createAccount(Long userId, CreateAccountRequest request) {
        long existingAccountsCount = accountRepository.countByUserIdAndAccountType(userId, request.getAccountType());
        
        if (request.getAccountType() == Account.AccountType.SAVINGS && existingAccountsCount >= 1) {
            throw new AccountAlreadyExistsException("User can only have one savings account");
        }
        
        if (request.getAccountType() == Account.AccountType.CHECKING && existingAccountsCount >= 3) {
            throw new AccountAlreadyExistsException("User can only have up to 3 checking accounts");
        }

        String accountNumber = generateAccountNumber();
        while (accountRepository.existsByAccountNumber(accountNumber)) {
            accountNumber = generateAccountNumber();
        }

        Account account = Account.builder()
                .userId(userId)
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(BigDecimal.ZERO)
                .status(Account.AccountStatus.ACTIVE)
                .build();

        account = accountRepository.save(account);

        if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
            AccountTransaction initialTransaction = AccountTransaction.builder()
                    .account(account)
                    .amount(BigDecimal.ZERO)
                    .transactionType(AccountTransaction.TransactionType.CREDIT)
                    .description("Account opened: " + request.getDescription())
                    .build();
            transactionRepository.save(initialTransaction);
        }

        return convertToDto(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getUserAccounts(Long userId) {
        List<Account> accounts = accountRepository.findActiveAccountsByUserId(userId);
        return accounts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));
        
        if (!account.getUserId().equals(userId)) {
            throw new AccountNotFoundException("Account not found or access denied");
        }
        
        return convertToDto(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountByNumber(String accountNumber, Long userId) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with number: " + accountNumber));
        
        if (!account.getUserId().equals(userId)) {
            throw new AccountNotFoundException("Account not found or access denied");
        }
        
        return convertToDto(account);
    }

    @Override
    public AccountDto closeAccount(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));
        
        if (!account.getUserId().equals(userId)) {
            throw new AccountNotFoundException("Account not found or access denied");
        }
        
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new InsufficientBalanceException("Cannot close account with non-zero balance");
        }
        
        account.setStatus(Account.AccountStatus.CLOSED);
        account = accountRepository.save(account);
        
        AccountTransaction closureTransaction = AccountTransaction.builder()
                .account(account)
                .amount(BigDecimal.ZERO)
                .transactionType(AccountTransaction.TransactionType.DEBIT)
                .description("Account closed")
                .build();
        transactionRepository.save(closureTransaction);
        
        return convertToDto(account);
    }

    @Override
    public TransactionDto addTransaction(Long accountId, Long userId, TransactionRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));
        
        if (!account.getUserId().equals(userId)) {
            throw new AccountNotFoundException("Account not found or access denied");
        }
        
        if (account.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new AccountNotFoundException("Cannot perform transaction on inactive account");
        }
        
        BigDecimal newBalance;
        if (request.getTransactionType() == AccountTransaction.TransactionType.CREDIT) {
            newBalance = account.getBalance().add(request.getAmount());
        } else {
            newBalance = account.getBalance().subtract(request.getAmount());
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientBalanceException("Insufficient balance for transaction");
            }
        }
        
        account.setBalance(newBalance);
        accountRepository.save(account);
        
        AccountTransaction transaction = AccountTransaction.builder()
                .account(account)
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .description(request.getDescription())
                .build();
        
        transaction = transactionRepository.save(transaction);
        
        return convertToTransactionDto(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDto> getAccountTransactions(Long accountId, Long userId, Pageable pageable) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));
        
        if (!account.getUserId().equals(userId)) {
            throw new AccountNotFoundException("Account not found or access denied");
        }
        
        Page<AccountTransaction> transactions = transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId, pageable);
        return transactions.map(this::convertToTransactionDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> getAccountTransactionHistory(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));
        
        if (!account.getUserId().equals(userId)) {
            throw new AccountNotFoundException("Account not found or access denied");
        }
        
        List<AccountTransaction> transactions = transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId);
        return transactions.stream()
                .map(this::convertToTransactionDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDto> getUserTransactions(Long userId, Pageable pageable) {
        Page<AccountTransaction> transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId, pageable);
        return transactions.map(this::convertToTransactionDto);
    }

    private String generateAccountNumber() {
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            accountNumber.append(secureRandom.nextInt(10));
        }
        return accountNumber.toString();
    }

    private AccountDto convertToDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .userId(account.getUserId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .build();
    }

    private TransactionDto convertToTransactionDto(AccountTransaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccount().getId())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .description(transaction.getDescription())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }
}