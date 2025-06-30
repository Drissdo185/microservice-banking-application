package com.example.card.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;


@Getter
@Setter
@Entity
@Table(name = "cards")
public class Cards extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('cards_card_id_seq')")
    @Column(name = "card_id", nullable = false)
    private Long id;

    @Size(max = 15)
    @NotNull
    @Column(name = "mobile_number", nullable = false, length = 15)
    private String mobileNumber;

    @Size(max = 100)
    @NotNull
    @Column(name = "card_number", nullable = false, length = 100)
    private String cardNumber;

    @Size(max = 100)
    @NotNull
    @Column(name = "card_type", nullable = false, length = 100)
    private String cardType;

    @NotNull
    @Column(name = "total_limit", nullable = false)
    private Integer totalLimit;

    @NotNull
    @Column(name = "amount_used", nullable = false)
    private Integer amountUsed;

    @NotNull
    @Column(name = "available_amount", nullable = false)
    private Integer availableAmount;


}