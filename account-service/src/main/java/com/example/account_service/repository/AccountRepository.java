package com.example.account_service.repository;

import com.example.account_service.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    List<Account> findByUserId(Long userId);
    
    Optional<Account> findByAccountNumber(String accountNumber);
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.status = 'ACTIVE'")
    List<Account> findActiveAccountsByUserId(@Param("userId") Long userId);
    
    boolean existsByAccountNumber(String accountNumber);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.userId = :userId AND a.accountType = :accountType")
    long countByUserIdAndAccountType(@Param("userId") Long userId, @Param("accountType") Account.AccountType accountType);
}