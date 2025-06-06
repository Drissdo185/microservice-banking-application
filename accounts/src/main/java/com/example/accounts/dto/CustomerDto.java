package com.example.accounts.dto;

import com.example.accounts.entity.Customer;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Customer}
 */
@Data
public class CustomerDto implements Serializable {
    String name;
    String email;
    String mobileNumber;

    private AccountDto accountDto;
}