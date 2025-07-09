package com.example.card_service.dto;


import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Data
public class CardDto {


    private Long id;
    private Long userId;
    private String cardNumber;
    private String cardHolderName;
    private Integer expiryMonth;
    private Integer expiryYear;
    private String cardType;
    private String cardStatus;
    private BigDecimal creditLimit;
    private BigDecimal currentBalance;
    private BigDecimal availableBalance;
}
