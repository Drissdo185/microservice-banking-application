package com.example.loan_service.repository;

import com.example.loan_service.entity.LoanPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Long> {
    
    List<LoanPayment> findByLoanIdOrderByPaymentDateDesc(Long loanId);
    
    Page<LoanPayment> findByLoanIdOrderByPaymentDateDesc(Long loanId, Pageable pageable);
    
    @Query("SELECT SUM(lp.paymentAmount) FROM LoanPayment lp WHERE lp.loanId = :loanId")
    BigDecimal getTotalPaymentsForLoan(@Param("loanId") Long loanId);
    
    @Query("SELECT lp FROM LoanPayment lp WHERE lp.loanId = :loanId AND lp.paymentDate BETWEEN :startDate AND :endDate")
    List<LoanPayment> findPaymentsByLoanIdAndDateRange(@Param("loanId") Long loanId, 
                                                      @Param("startDate") LocalDateTime startDate, 
                                                      @Param("endDate") LocalDateTime endDate);
}