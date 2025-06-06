package com.example.accounts.service.impl;

//import com.example.accounts.constant.AccountsConstant;
import com.example.accounts.constant.AccountsConstant;
import com.example.accounts.dto.CustomerDto;
import com.example.accounts.entity.Account;
import com.example.accounts.entity.Customer;
import com.example.accounts.exception.CustomerAlreadyExistsException;
import com.example.accounts.mapper.CustomerMapper;
import com.example.accounts.repository.AccountRepository;
import com.example.accounts.repository.CustomerRepository;
import com.example.accounts.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;


    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapperToCustomer(customerDto, new Customer());
        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customerDto.getMobileNumber());
        if(optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with mobileNumber "+customerDto.getMobileNumber());
        }
        Customer savedCustomer =  customerRepository.save(customer);
        accountRepository.save(createNewAccount(savedCustomer));

    }

    private Account createNewAccount(Customer customer) {
        Account newAccount = new Account();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);

        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstant.SAVINGS);
        newAccount.setBranchAddress("123 Hoc Mon");
        return newAccount;
    }


}
