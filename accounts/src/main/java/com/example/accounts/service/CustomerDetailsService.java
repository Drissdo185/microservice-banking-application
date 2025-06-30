package com.example.accounts.service;

import com.example.accounts.dto.CustomerDetailsDto;
import com.example.accounts.dto.CustomerDto;
import com.example.accounts.dto.CardsDto;
import com.example.accounts.dto.LoansDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerDetailsService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AccountService accountService;

    public CustomerDetailsDto getCustomerDetails(String mobileNumber) {
        
        CustomerDto customerDto = accountService.fetchAccount(mobileNumber);
        
        CustomerDetailsDto customerDetailsDto = new CustomerDetailsDto();
        customerDetailsDto.setCustomerDto(customerDto);

        
        try {
            CardsDto cardsDto = restTemplate.getForObject(
                "http://cards/api/v1/cards/fetch?mobileNumber=" + mobileNumber, 
                CardsDto.class
            );
            customerDetailsDto.setCardsDto(cardsDto);
        } catch (Exception e) {
            System.out.println("Cards service not available for customer: " + mobileNumber);
        }

        // Get loans details using service discovery
        try {
            LoansDto loansDto = restTemplate.getForObject(
                "http://loans/api/v1/loans/fetch?mobileNumber=" + mobileNumber, 
                LoansDto.class
            );
            customerDetailsDto.setLoansDto(loansDto);
        } catch (Exception e) {
            System.out.println("Loans service not available for customer: " + mobileNumber);
        }

        return customerDetailsDto;
    }
}