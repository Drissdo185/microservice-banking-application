package com.example.accounts.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "accounts")
public class Account extends BaseEntity{
    @Id
    @Column(name = "account_number", nullable = false)
    private Long accountNumber;

    @Column(name="customer_id")
    private UUID customerId;

    @Column(name = "account_type", nullable = false, length = 100)
    private String accountType;

    @Column(name = "branch_address", nullable = false, length = 200)
    private String branchAddress;


}