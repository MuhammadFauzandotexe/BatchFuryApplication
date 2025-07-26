package com.mdro.BatchFury.processor;

import com.mdro.BatchFury.model.Transaction;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TransactionItemProcessor implements ItemProcessor<Transaction, Transaction> {

    @Override
    public Transaction process(Transaction transaction) throws Exception {
        // Contoh processing: ubah status menjadi uppercase
        if (transaction.getStatus() != null) {
            transaction.setStatus(transaction.getStatus().toUpperCase());
        }

        // Log data yang diproses
        System.out.println("Processing transaction: " + transaction);

        // Return data yang sudah diproses
        return transaction;
    }
}