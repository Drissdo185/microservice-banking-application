package com.example.accounts.mapper;

import com.example.accounts.dto.CustomerDto;
import com.example.accounts.entity.Customer;

public class CustomerMapper {

    public static CustomerDto mapperToCustomerDto(Customer customer, CustomerDto customerDto) {
        customerDto.setName(customerDto.getName());
        customerDto.setEmail(customerDto.getEmail());
        customerDto.setMobileNumber(customerDto.getMobileNumber());
        return customerDto;
    }

    public static Customer mapperToCustomer(CustomerDto customerDto, Customer customer) {
        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        customer.setMobileNumber(customerDto.getMobileNumber());
        return customer;
    }
}
