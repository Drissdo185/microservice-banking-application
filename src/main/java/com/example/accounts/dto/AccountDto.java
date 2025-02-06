package com.example.accounts.dto;

import com.example.accounts.entity.Account;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link Account}
 */
@Data
public class AccountDto implements Serializable {
    Long accountNumber;
    String accountType;
    String branchAddress;
}