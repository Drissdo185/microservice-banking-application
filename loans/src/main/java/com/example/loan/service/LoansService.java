package com.example.loan.service;


import com.example.loan.dto.LoansDto;

public interface LoansService {

    void createLoan(String mobile);

    LoansDto fetchLoan(String mobile);

    boolean updateLoan(LoansDto cardsDto);

    boolean deleteLoan(String mobile);
}
