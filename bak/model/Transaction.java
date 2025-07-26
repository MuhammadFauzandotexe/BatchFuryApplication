package com.mdro.BatchFury.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
public class Transaction {
    private Long id;
    private LocalDateTime transactionDate;
    private String referenceNumber;
    private BigDecimal amount;
    private String accountNumber;
    private String status;
    private LocalDate partitionDate;
}
