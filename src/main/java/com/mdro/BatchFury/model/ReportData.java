package com.mdro.BatchFury.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class ReportData {
    private String referenceNumber;
    private String transactionDate;
    private BigDecimal amount;
    private String accountNumber;
    private String status;

    @Override
    public String toString() {
        return "ReportData{" +
                "referenceNumber='" + referenceNumber + '\'' +
                ", transactionDate='" + transactionDate + '\'' +
                ", amount=" + amount +
                ", accountNumber='" + accountNumber + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
