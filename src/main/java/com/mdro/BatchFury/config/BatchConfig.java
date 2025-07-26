package com.mdro.BatchFury.config;


import com.mdro.BatchFury.model.Transaction;
import com.mdro.BatchFury.processor.TransactionItemProcessor;
import com.mdro.BatchFury.writer.TransactionItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;


@Configuration
public class BatchConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TransactionItemProcessor processor;

    @Autowired
    private TransactionItemWriter writer;

    @Bean
    public JdbcCursorItemReader<Transaction> transactionReader() {
        return new JdbcCursorItemReaderBuilder<Transaction>()
                .name("transactionReader")
                .dataSource(dataSource)
                .sql("SELECT id, transaction_date, reference_number, amount, " +
                        "account_number, status, partition_date FROM transactions " +
                        "ORDER BY id")
                .rowMapper(new TransactionRowMapper())
                .build();
    }

    @Bean
    public Step processTransactionsStep(JobRepository jobRepository,
                                        PlatformTransactionManager transactionManager) {
        return new StepBuilder("processTransactionsStep", jobRepository)
                .<Transaction, Transaction>chunk(10, transactionManager)
                .reader(transactionReader())
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job processTransactionsJob(JobRepository jobRepository,
                                      Step processTransactionsStep) {
        return new JobBuilder("processTransactionsJob", jobRepository)
                .start(processTransactionsStep)
                .build();
    }

    // RowMapper untuk mapping ResultSet ke Transaction object
    public static class TransactionRowMapper implements RowMapper<Transaction> {
        @Override
        public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            Transaction transaction = new Transaction();
            transaction.setId(rs.getLong("id"));
            transaction.setTransactionDate(rs.getTimestamp("transaction_date") != null ?
                    rs.getTimestamp("transaction_date") : null);
            transaction.setReferenceNumber(rs.getString("reference_number"));
            transaction.setAmount(rs.getBigDecimal("amount"));
            transaction.setAccountNumber(rs.getString("account_number"));
            transaction.setStatus(rs.getString("status"));
            transaction.setPartitionDate(rs.getDate("partition_date") != null ?
                    rs.getDate("partition_date") : null);
            return transaction;
        }
    }
}