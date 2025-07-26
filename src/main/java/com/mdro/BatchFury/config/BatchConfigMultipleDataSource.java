package com.mdro.BatchFury.config;


import com.mdro.BatchFury.listener.ChunkProgressListener;
import com.mdro.BatchFury.listener.JobCompletionNotificationListener;
import com.mdro.BatchFury.listener.StepExecutionInfoListener;
import com.mdro.BatchFury.model.Transaction;
import com.mdro.BatchFury.processor.TransactionItemProcessor;
import com.mdro.BatchFury.writer.ReportWriter;
import com.mdro.BatchFury.writer.TransactionItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;


@Configuration
public class BatchConfigMultipleDataSource extends DefaultBatchConfiguration {

    private final DataSource batchDataSource;

    public BatchConfigMultipleDataSource(@Qualifier("dataSource") DataSource batchDataSource) {
        this.batchDataSource = batchDataSource;
    }

    // ✅ Define TransactionManager bean explicitly
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(batchDataSource);
    }

    @Autowired
    private TransactionItemProcessor processor;

    @Autowired
    private TransactionItemWriter writer;

    @Value("${batch.chunk-size:500}")
    private int chunkSize;

    @Value("${batch.thread-pool-size:4}")
    private int threadPoolSize;

    @Autowired
    private JobCompletionNotificationListener jobListener;

    @Autowired
    private StepExecutionInfoListener stepListener;

    @Autowired
    private ChunkProgressListener chunkListener;

    @Autowired
    private ReportWriter reportWriter;

    @Bean
    public JdbcCursorItemReader<Transaction> transactionReader() {
        return new JdbcCursorItemReaderBuilder<Transaction>()
                .name("transactionReader")
                .dataSource(batchDataSource)
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
                .<Transaction, Transaction>chunk(chunkSize, transactionManager)
                .reader(transactionReader())
                .processor(processor)
                .listener(stepListener)
                .listener(chunkListener)
                .writer(reportWriter.simpleReportFileWriterFromTransaction(null))
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