package com.example.loan_service.service.impl;

import com.example.loan_service.client.UserServiceClient;
import com.example.loan_service.dto.LoanDto;
import com.example.loan_service.dto.LoanPaymentDto;
import com.example.loan_service.dto.UserValidationResponse;
import com.example.loan_service.entity.Loan;
import com.example.loan_service.entity.LoanPayment;
import com.example.loan_service.exception.InsufficientBalanceException;
import com.example.loan_service.exception.LoanNotFoundException;
import com.example.loan_service.exception.UserNotFoundException;
import com.example.loan_service.repository.LoanPaymentRepository;
import com.example.loan_service.repository.LoanRepository;
import com.example.loan_service.service.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public LoanDto createLoan(LoanDto loanDto, String token) {
        UserValidationResponse userValidation = userServiceClient.validateUser(token).block();
        
        if (userValidation == null || !userValidation.isValid()) {
            throw new UserNotFoundException("User not found or invalid");
        }
        
        if (loanRepository.countActiveLoansForUser(loanDto.getUserId()) >= 3) {
            throw new RuntimeException("User cannot have more than 3 active loans");
        }
        
        BigDecimal emi = calculateEmi(loanDto.getLoanAmount(), loanDto.getInterestRate(), loanDto.getTenureMonths());
        
        Loan loan = new Loan();
        loan.setUserId(loanDto.getUserId());
        loan.setLoanAmount(loanDto.getLoanAmount());
        loan.setInterestRate(loanDto.getInterestRate());
        loan.setTenureMonths(loanDto.getTenureMonths());
        loan.setMonthlyEmi(emi);
        loan.setOutstandingAmount(loanDto.getLoanAmount());
        loan.setStatus(Loan.LoanStatus.ACTIVE);
        
        Loan savedLoan = loanRepository.save(loan);
        log.info("Created new loan with ID: {} for user: {}", savedLoan.getId(), savedLoan.getUserId());
        
        return convertToDto(savedLoan);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanDto getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + id));
        return convertToDto(loan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanDto> getLoansByUserId(Long userId) {
        List<Loan> loans = loanRepository.findByUserId(userId);
        return loans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanDto> getAllLoans(Pageable pageable) {
        Page<Loan> loans = loanRepository.findAll(pageable);
        return loans.map(this::convertToDto);
    }

    @Override
    public LoanDto updateLoan(Long id, LoanDto loanDto) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + id));
        
        if (loanDto.getInterestRate() != null) {
            loan.setInterestRate(loanDto.getInterestRate());
        }
        if (loanDto.getTenureMonths() != null) {
            loan.setTenureMonths(loanDto.getTenureMonths());
        }
        
        BigDecimal newEmi = calculateEmi(loan.getLoanAmount(), loan.getInterestRate(), loan.getTenureMonths());
        loan.setMonthlyEmi(newEmi);
        
        Loan updatedLoan = loanRepository.save(loan);
        log.info("Updated loan with ID: {}", updatedLoan.getId());
        
        return convertToDto(updatedLoan);
    }

    @Override
    public void deleteLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + id));
        
        if (loan.getStatus() == Loan.LoanStatus.ACTIVE && loan.getOutstandingAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Cannot delete loan with outstanding balance");
        }
        
        loanRepository.delete(loan);
        log.info("Deleted loan with ID: {}", id);
    }

    @Override
    public LoanDto updateLoanStatus(Long id, String status) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + id));
        
        try {
            Loan.LoanStatus newStatus = Loan.LoanStatus.valueOf(status.toUpperCase());
            loan.setStatus(newStatus);
            
            Loan updatedLoan = loanRepository.save(loan);
            log.info("Updated loan status to {} for loan ID: {}", newStatus, id);
            
            return convertToDto(updatedLoan);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid loan status: " + status);
        }
    }

    @Override
    public LoanPaymentDto makePayment(Long loanId, BigDecimal amount) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found with ID: " + loanId));
        
        if (loan.getStatus() != Loan.LoanStatus.ACTIVE) {
            throw new RuntimeException("Cannot make payment to non-active loan");
        }
        
        if (amount.compareTo(loan.getOutstandingAmount()) > 0) {
            throw new InsufficientBalanceException("Payment amount cannot exceed outstanding balance");
        }
        
        LoanPayment payment = new LoanPayment();
        payment.setLoanId(loanId);
        payment.setPaymentAmount(amount);
        
        LoanPayment savedPayment = loanPaymentRepository.save(payment);
        
        BigDecimal newOutstanding = loan.getOutstandingAmount().subtract(amount);
        loan.setOutstandingAmount(newOutstanding);
        
        if (newOutstanding.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(Loan.LoanStatus.PAID);
        }
        
        loanRepository.save(loan);
        log.info("Made payment of {} for loan ID: {}, new outstanding: {}", amount, loanId, newOutstanding);
        
        return convertPaymentToDto(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanPaymentDto> getPaymentsByLoanId(Long loanId) {
        List<LoanPayment> payments = loanPaymentRepository.findByLoanIdOrderByPaymentDateDesc(loanId);
        return payments.stream()
                .map(this::convertPaymentToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal calculateEmi(BigDecimal loanAmount, BigDecimal interestRate, Integer tenureMonths) {
        if (interestRate.compareTo(BigDecimal.ZERO) == 0) {
            return loanAmount.divide(BigDecimal.valueOf(tenureMonths), 2, RoundingMode.HALF_UP);
        }
        
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(100 * 12), 10, RoundingMode.HALF_UP);
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal numerator = loanAmount.multiply(monthlyRate).multiply(onePlusRate.pow(tenureMonths));
        BigDecimal denominator = onePlusRate.pow(tenureMonths).subtract(BigDecimal.ONE);
        
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanDto> getActiveLoansByUserId(Long userId) {
        List<Loan> loans = loanRepository.findByUserIdAndStatus(userId, Loan.LoanStatus.ACTIVE);
        return loans.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasEligibleCreditScore(Long userId) {
        return true;
    }

    private LoanDto convertToDto(Loan loan) {
        LoanDto dto = new LoanDto();
        dto.setId(loan.getId());
        dto.setUserId(loan.getUserId());
        dto.setLoanAmount(loan.getLoanAmount());
        dto.setInterestRate(loan.getInterestRate());
        dto.setTenureMonths(loan.getTenureMonths());
        dto.setMonthlyEmi(loan.getMonthlyEmi());
        dto.setOutstandingAmount(loan.getOutstandingAmount());
        dto.setStatus(loan.getStatus());
        dto.setCreatedAt(loan.getCreatedAt());
        return dto;
    }

    private LoanPaymentDto convertPaymentToDto(LoanPayment payment) {
        LoanPaymentDto dto = new LoanPaymentDto();
        dto.setId(payment.getId());
        dto.setLoanId(payment.getLoanId());
        dto.setPaymentAmount(payment.getPaymentAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        return dto;
    }
}