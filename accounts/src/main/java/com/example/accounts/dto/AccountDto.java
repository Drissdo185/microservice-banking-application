package com.example.accounts.dto;

import com.example.accounts.entity.Account;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link Account}
 */
@Data
public class AccountDto implements Serializable {

    @NotEmpty(message = "Account number can not be a null or empty")
    @Pattern(regexp="^[0-9]{10}$", message = "Account number must be 10 digits")
    Long accountNumber;

    @NotEmpty(message = "Account number can not be a null or empty")
    String accountType;

    @NotEmpty(message = "Account number can not be a null or empty")
    String branchAddress;
}