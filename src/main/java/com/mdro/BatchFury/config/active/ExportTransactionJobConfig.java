package com.mdro.BatchFury.config.active;

import com.mdro.BatchFury.constant.BatchBeanNames;
import com.mdro.BatchFury.listener.ChunkProgressListener;
import com.mdro.BatchFury.listener.JobCompletionNotificationListener;
import com.mdro.BatchFury.listener.StepExecutionInfoListener;
import com.mdro.BatchFury.model.Transaction;
import com.mdro.BatchFury.processor.TransactionItemProcessor;
import com.mdro.BatchFury.writer.ReportWriter;

import org.springframework.batch.core.*;
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
public class ExportTransactionJobConfig {

    private final DataSource remoteDatasource;

    public ExportTransactionJobConfig(@Qualifier("remoteDataSource") DataSource remoteDatasource) {
        this.remoteDatasource = remoteDatasource;
    }

    @Value("${batch.chunk-size:500}")
    private int chunkSize;

    @Autowired private TransactionItemProcessor processor;
    @Autowired private JobCompletionNotificationListener jobListener;
    @Autowired private StepExecutionInfoListener stepListener;
    @Autowired private ChunkProgressListener chunkListener;
    @Autowired private ReportWriter reportWriter;

    @Bean(name = "remoteTransactionReader")
    public JdbcCursorItemReader<Transaction> transactionReader() {
        return new JdbcCursorItemReaderBuilder<Transaction>()
                .name("remoteTransactionReader")
                .dataSource(remoteDatasource)
                .sql("SELECT id, transaction_date, reference_number, amount, " +
                        "account_number, status, partition_date FROM transactions ORDER BY id")
                .rowMapper(new TransactionRowMapper())
                .build();
    }

    @Bean(name = "remoteProcessTransactionsStep")
    public Step remoteProcessTransactionsStep(
            @Qualifier(BatchBeanNames.JOB_REPOSITORY) JobRepository jobRepository,
            @Qualifier("remoteTransactionManager") PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("remoteProcessTransactionsStep", jobRepository)
                .<Transaction, Transaction>chunk(chunkSize, transactionManager)
                .reader(transactionReader())
                .processor(processor)
                .listener(stepListener)
                .listener(chunkListener)
                .writer(reportWriter.simpleReportFileWriterFromTransaction(null))
                .build();
    }

    @Bean(name = "processRemoteTransactionsJob")
    public Job processRemoteTransactionsJob(
            @Qualifier(BatchBeanNames.JOB_REPOSITORY) JobRepository jobRepository,
            @Qualifier("remoteProcessTransactionsStep") Step remoteStep
    ) {
        return new JobBuilder("processRemoteTransactionsJob", jobRepository)
                .listener(jobListener)
                .start(remoteStep)
                .build();
    }

    public static class TransactionRowMapper implements RowMapper<Transaction> {
        @Override
        public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            Transaction transaction = new Transaction();
            transaction.setId(rs.getLong("id"));
            transaction.setTransactionDate(rs.getTimestamp("transaction_date"));
            transaction.setReferenceNumber(rs.getString("reference_number"));
            transaction.setAmount(rs.getBigDecimal("amount"));
            transaction.setAccountNumber(rs.getString("account_number"));
            transaction.setStatus(rs.getString("status"));
            transaction.setPartitionDate(rs.getDate("partition_date"));
            return transaction;
        }
    }
}
