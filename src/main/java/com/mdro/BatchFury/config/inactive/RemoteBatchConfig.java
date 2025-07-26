//package com.mdro.BatchFury.config;
//
//import com.mdro.BatchFury.listener.ChunkProgressListener;
//import com.mdro.BatchFury.listener.JobCompletionNotificationListener;
//import com.mdro.BatchFury.listener.StepExecutionInfoListener;
//import com.mdro.BatchFury.model.Transaction;
//import com.mdro.BatchFury.processor.TransactionItemProcessor;
//import com.mdro.BatchFury.writer.ReportWriter;
//
//import org.springframework.batch.core.*;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.explore.JobExplorer;
//import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.launch.support.SimpleJobLauncher;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.item.database.JdbcCursorItemReader;
//import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import javax.sql.DataSource;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//@Configuration
//@EnableBatchProcessing
//public class RemoteBatchConfig {
//
//    // ✅ primaryDataSource = tempat menyimpan metadata Spring Batch (DB Remote B)
//    private final DataSource primaryDataSource;
//
//    // ✅ remoteDatasource = tempat ambil data transaksi (DB Remote B, bisa beda schema)
//    private final DataSource remoteDatasource;
//
//    public RemoteBatchConfig(@Qualifier("localDataSource") DataSource primaryDataSource,
//                             @Qualifier("remoteDataSource") DataSource remoteDatasource) {
//        this.primaryDataSource = primaryDataSource;
//        this.remoteDatasource = remoteDatasource;
//    }
//
//    @Value("${batch.chunk-size:500}")
//    private int chunkSize;
//
//    @Autowired private TransactionItemProcessor processor;
//    @Autowired private JobCompletionNotificationListener jobListener;
//    @Autowired private StepExecutionInfoListener stepListener;
//    @Autowired private ChunkProgressListener chunkListener;
//    @Autowired private ReportWriter reportWriter;
//
//    // ✅ TransactionManager untuk job step (akses transaksi dari remoteDatasource)
//    @Bean(name = "remoteTransactionManager")
//    public PlatformTransactionManager remoteTransactionManager() {
//        return new DataSourceTransactionManager(remoteDatasource);
//    }
//
//    @Bean(name = "primaryTransactionManager")
//    public PlatformTransactionManager primaryTransactionManager() {
//        return new DataSourceTransactionManager(primaryDataSource);
//    }
//
//    // ✅ JobRepository menyimpan metadata ke primaryDataSource (DB Remote B)
//    @Bean(name = "remoteJobRepository")
//    public JobRepository jobRepository() throws Exception {
//        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
//        factory.setDataSource(primaryDataSource);
//        factory.setTransactionManager(new DataSourceTransactionManager(primaryDataSource));
//        factory.afterPropertiesSet();
//        return factory.getObject();
//    }
//
//    // ✅ JobLauncher pakai JobRepository di atas
//    @Bean(name = "remoteJobLauncher")
//    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
//        SimpleJobLauncher launcher = new SimpleJobLauncher();
//        launcher.setJobRepository(jobRepository);
//        launcher.afterPropertiesSet();
//        return launcher;
//    }
//
//    // ✅ JobExplorer untuk lihat histori job dari DB metadata
//    @Bean(name = "remoteJobExplorer")
//    public JobExplorer jobExplorer() throws Exception {
//        JobExplorerFactoryBean factoryBean = new JobExplorerFactoryBean();
//        factoryBean.setDataSource(primaryDataSource);
//        factoryBean.setTransactionManager(new DataSourceTransactionManager(primaryDataSource)); // ✅ tambahkan ini
//        factoryBean.afterPropertiesSet();
//        return factoryBean.getObject();
//    }
//
//    // ✅ Reader baca transaksi dari schema tertentu (misalnya `reporting.transactions`)
//    @Bean(name = "remoteTransactionReader(")
//    public JdbcCursorItemReader<Transaction> transactionReader() {
//        return new JdbcCursorItemReaderBuilder<Transaction>()
//                .name("remoteTransactionReader")
//                .dataSource(remoteDatasource)
//                .sql("SELECT id, transaction_date, reference_number, amount, " +
//                        "account_number, status, partition_date FROM transactions ORDER BY id")
//                .rowMapper(new TransactionRowMapper())
//                .build();
//    }
//
//    // ✅ Step proses transaksi dari remote DB
//    @Bean(name = "remoteProcessTransactionsStep")
//    public Step remoteProcessTransactionsStep(JobRepository jobRepository,
//                                              @Qualifier("remoteTransactionManager") PlatformTransactionManager transactionManager) {
//        return new StepBuilder("remoteProcessTransactionsStep", jobRepository)
//                .<Transaction, Transaction>chunk(chunkSize, transactionManager)
//                .reader(transactionReader())
//                .processor(processor)
//                .listener(stepListener)
//                .listener(chunkListener)
//                .writer(reportWriter.simpleReportFileWriterFromTransaction(null))
//                .build();
//    }
//
//    // ✅ Job utama untuk remote
//    @Bean(name = "processRemoteTransactionsJob")
//    public Job processRemoteTransactionsJob(JobRepository jobRepository,
//                                            @Qualifier("remoteProcessTransactionsStep") Step remoteStep) {
//        return new JobBuilder("processRemoteTransactionsJob", jobRepository)
//                .listener(jobListener)
//                .start(remoteStep)
//                .build();
//    }
//
//
//    // ✅ RowMapper
//    public static class TransactionRowMapper implements RowMapper<Transaction> {
//        @Override
//        public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
//            Transaction transaction = new Transaction();
//            transaction.setId(rs.getLong("id"));
//            transaction.setTransactionDate(rs.getTimestamp("transaction_date"));
//            transaction.setReferenceNumber(rs.getString("reference_number"));
//            transaction.setAmount(rs.getBigDecimal("amount"));
//            transaction.setAccountNumber(rs.getString("account_number"));
//            transaction.setStatus(rs.getString("status"));
//            transaction.setPartitionDate(rs.getDate("partition_date"));
//            return transaction;
//        }
//    }
//}
