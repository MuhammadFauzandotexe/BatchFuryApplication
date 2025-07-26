package com.mdro.BatchFury.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class Transaction {
    private Long id;
    private Date transactionDate;
    private String referenceNumber;
    private BigDecimal amount;
    private String accountNumber;
    private String status;
    private Date partitionDate;

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", transactionDate=" + transactionDate +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", amount=" + amount +
                ", accountNumber='" + accountNumber + '\'' +
                ", status='" + status + '\'' +
                ", partitionDate=" + partitionDate +
                '}';
    }

}