package com.example.card_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CardDto {

    private Long id;

    private Long userId;

    private String cardNumber;

    @NotBlank(message = "Card holder name is required")
    @Size(max = 100, message = "Card holder name must not exceed 100 characters")
    private String cardHolderName;

    @NotNull(message = "Expiry month is required")
    @Min(value = 1, message = "Expiry month must be between 1 and 12")
    @Max(value = 12, message = "Expiry month must be between 1 and 12")
    private Integer expiryMonth;

    @NotNull(message = "Expiry year is required")
    @Min(value = 2024, message = "Expiry year must be current year or later")
    private Integer expiryYear;

    @NotBlank(message = "Card type is required")
    @Pattern(regexp = "^(CREDIT|DEBIT)$", message = "Card type must be either CREDIT or DEBIT")
    private String cardType;

    @Pattern(regexp = "^(ACTIVE|BLOCKED|EXPIRED)$", message = "Card status must be ACTIVE, BLOCKED, or EXPIRED")
    private String cardStatus;

    @NotNull(message = "Credit limit is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Credit limit must be positive")
    private BigDecimal creditLimit;

    @NotNull(message = "Current balance is required")
    @DecimalMin(value = "0.0", message = "Current balance must be non-negative")
    private BigDecimal currentBalance;

    @NotNull(message = "Available balance is required")
    @DecimalMin(value = "0.0", message = "Available balance must be non-negative")
    private BigDecimal availableBalance;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updatedAt;
}
