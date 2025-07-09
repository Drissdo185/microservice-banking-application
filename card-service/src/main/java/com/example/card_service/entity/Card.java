package com.example.card_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Size(max = 19)
    @NotNull
    @Column(name = "card_number", nullable = false, length = 19)
    private String cardNumber;

    @Size(max = 100)
    @NotNull
    @Column(name = "card_holder_name", nullable = false, length = 100)
    private String cardHolderName;

    @NotNull
    @Column(name = "expiry_month", nullable = false)
    private Integer expiryMonth;

    @NotNull
    @Column(name = "expiry_year", nullable = false)
    private Integer expiryYear;

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'CREDIT'")
    @Column(name = "card_type", nullable = false, length = 20)
    private String cardType;

    @Size(max = 20)
    @ColumnDefault("'ACTIVE'")
    @Column(name = "card_status", length = 20)
    private String cardStatus;

    @NotNull
    @ColumnDefault("5000.00")
    @Column(name = "credit_limit", nullable = false, precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @NotNull
    @ColumnDefault("0.00")
    @Column(name = "current_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentBalance;

    @NotNull
    @ColumnDefault("5000.00")
    @Column(name = "available_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal availableBalance;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}