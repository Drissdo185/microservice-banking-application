package com.example.account_service.repository;

import com.example.account_service.entity.AccountTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccountTransactionRepository extends JpaRepository<AccountTransaction, Long> {
    
    Page<AccountTransaction> findByAccountIdOrderByTransactionDateDesc(Long accountId, Pageable pageable);
    
    List<AccountTransaction> findByAccountIdOrderByTransactionDateDesc(Long accountId);
    
    @Query("SELECT t FROM AccountTransaction t WHERE t.account.id = :accountId AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<AccountTransaction> findByAccountIdAndDateRange(@Param("accountId") Long accountId, 
                                                        @Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM AccountTransaction t WHERE t.account.userId = :userId ORDER BY t.transactionDate DESC")
    Page<AccountTransaction> findByUserIdOrderByTransactionDateDesc(@Param("userId") Long userId, Pageable pageable);
}