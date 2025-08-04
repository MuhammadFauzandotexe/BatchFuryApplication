package com.mdro.BatchFury.proccesor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;

@Component
public class TransactionProcessor implements ItemProcessor<Transaction, ReportData> {

    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessor.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public ReportData process(Transaction transaction) throws Exception {
        // Simulate processing logic and potential errors
        // Example: Skip records with negative amount
        if (transaction.getAmount().signum() < 0) {
            logger.warn("Skipping record with negative amount for transaction: {}", transaction.getReferenceNumber());
            return null; // Returning null skips the item
        }

        // Transform Transaction to ReportData
        ReportData reportData = new ReportData();
        reportData.setReferenceNumber(transaction.getReferenceNumber());
        reportData.setTransactionDate(transaction.getTransactionDate().format(DATE_FORMATTER));
        reportData.setAmount(transaction.getAmount());
        reportData.setAccountNumber(transaction.getAccountNumber());
        reportData.setStatus(transaction.getStatus());

        return reportData;
    }
}
