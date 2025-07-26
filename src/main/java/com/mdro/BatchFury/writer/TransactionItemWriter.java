package com.mdro.BatchFury.writer;

import com.mdro.BatchFury.model.Transaction;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class TransactionItemWriter implements ItemWriter<Transaction> {

    @Override
    public void write(Chunk<? extends Transaction> transactions) throws Exception {
        // Contoh writer sederhana yang hanya print ke console
        // Anda bisa menggantinya dengan write ke database, file, atau sistem lain
        System.out.println("Writing " + transactions.size() + " transactions:");

        for (Transaction transaction : transactions) {
            System.out.println("Written: " + transaction);
        }

        System.out.println("Batch write completed for " + transactions.size() + " items");
    }
}