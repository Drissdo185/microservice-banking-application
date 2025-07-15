package com.example.loan_service.repository;

import com.example.loan_service.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    
    List<Loan> findByUserId(Long userId);
    
    Page<Loan> findByUserId(Long userId, Pageable pageable);
    
    List<Loan> findByStatus(Loan.LoanStatus status);
    
    @Query("SELECT l FROM Loan l WHERE l.userId = :userId AND l.status = :status")
    List<Loan> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Loan.LoanStatus status);
    
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.userId = :userId AND l.status = 'ACTIVE'")
    long countActiveLoansForUser(@Param("userId") Long userId);
}