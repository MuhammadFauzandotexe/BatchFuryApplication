package com.mdro.BatchFury.profile;

import com.mdro.BatchFury.constant.BatchBeanNames;
import com.mdro.BatchFury.constant.DataAccessBeanNames;
import com.mdro.BatchFury.listener.ChunkProgressListener;
import com.mdro.BatchFury.listener.JobCompletionNotificationListener;
import com.mdro.BatchFury.listener.StepExecutionInfoListener;
import com.mdro.BatchFury.model.Transaction;
import com.mdro.BatchFury.processor.TransactionItemProcessor;
import com.mdro.BatchFury.faultTolerant.BaseSkipPolicy;
import com.mdro.BatchFury.writer.ReportWriter;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
@Slf4j
@Configuration
public class ExportLocalTransactionConfig {

    private final DataSource localDatasource;

    public ExportLocalTransactionConfig(@Qualifier(DataAccessBeanNames.DataSource.LOCAL)
                                        DataSource localDatasource) {
        this.localDatasource = localDatasource;
    }

    @Value("${batch.chunk-size:500}")
    private int chunkSize;

    @Autowired private TransactionItemProcessor processor;
    @Autowired private JobCompletionNotificationListener jobListener;
    @Autowired private StepExecutionInfoListener stepListener;
    @Autowired private ChunkProgressListener chunkListener;
    @Autowired private ReportWriter reportWriter;
    @Autowired private BaseSkipPolicy baseSkipPolicy;

    @Bean(name = "localTransactionReader")
    public JdbcCursorItemReader<Transaction> transactionReader() {
        String sql = "SELECT id, transaction_date, reference_number, amount, " +
                "account_number, status, partition_date FROM transactions ORDER BY id";

        log.info("SQL Reader Query (Local): {}", sql);
        return new JdbcCursorItemReaderBuilder<Transaction>()
                .name("localTransactionReader")
                .dataSource(localDatasource)
                .sql(sql)
                .rowMapper(new TransactionRowMapper())
                .build();
    }

    @Bean(name = "localProcessTransactionsStep")
    public Step localProcessTransactionsStep(
            @Qualifier(BatchBeanNames.JOB_REPOSITORY) JobRepository jobRepository,
            @Qualifier(DataAccessBeanNames.TransactionManager.LOCAL) PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("localProcessTransactionsStep", jobRepository)
                .<Transaction, Transaction>chunk(chunkSize, transactionManager)
                .reader(transactionReader())
                .processor(processor)
                .listener(stepListener)
                .listener(chunkListener)
                .writer(reportWriter.simpleReportFileWriterFromTransaction(null))
                .faultTolerant().skipPolicy(baseSkipPolicy)
                .build();
    }

    @Bean(name = "processLocalTransactionsJob")
    public Job processLocalTransactionsJob(
            @Qualifier(BatchBeanNames.JOB_REPOSITORY) JobRepository jobRepository,
            @Qualifier("localProcessTransactionsStep") Step localStep
    ) {
        return new JobBuilder("processLocalTransactionsJob", jobRepository)
                .listener(jobListener)
                .start(localStep)
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
