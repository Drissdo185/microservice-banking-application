package com.example.accounts.dto;

import com.example.accounts.entity.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;



/**
 * DTO for {@link Account}
 */
@Data
@Schema(
        name = "Accounts",
        description = "Schema to hold Account information"
)
public class AccountDto implements Serializable {

    @NotEmpty(message = "Account number can not be a null or empty")
    @Pattern(regexp="^[0-9]{10}$", message = "Account number must be 10 digits")
    @Schema(
            description = "Account Number of Eazy Bank account", example = "3454433243"
    )
    Long accountNumber;

    @NotEmpty(message = "Account number can not be a null or empty")
    @Schema(
            description = "Account type of Eazy Bank account", example = "Savings"
    )
    String accountType;

    @NotEmpty(message = "Account number can not be a null or empty")
    @Schema(
            description = "Eazy Bank branch address", example = "123 NewYork"
    )
    String branchAddress;
}