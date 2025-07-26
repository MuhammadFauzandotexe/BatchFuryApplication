package com.mdro.BatchFury.reader;


import com.mdro.BatchFury.model.Transaction;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component // Tandai sebagai Spring Component
public class TransactionReader {

    @Autowired
    private DataSource dataSource;

    // RowMapper tetap sama
    private static class TransactionRowMapper implements RowMapper<Transaction> {
        @Override
        public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            Transaction transaction = new Transaction();
            transaction.setId(rs.getLong("id"));
            transaction.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());
            transaction.setReferenceNumber(rs.getString("reference_number"));
            transaction.setAmount(rs.getBigDecimal("amount"));
            transaction.setAccountNumber(rs.getString("account_number"));
            transaction.setStatus(rs.getString("status"));
            transaction.setPartitionDate(rs.getDate("partition_date").toLocalDate());
            return transaction;
        }
    }

    /**
     * Reader untuk Partitioning Steps. Menggunakan JdbcPagingItemReader.
     * @param partitionStart Tanggal mulai partisi (dari StepExecutionContext)
     * @param partitionEnd Tanggal akhir partisi (dari StepExecutionContext)
     * @param chunkSize Ukuran chunk dari konfigurasi aplikasi
     * @return JdbcPagingItemReader
     * @throws Exception jika ada masalah dalam membuat PagingQueryProvider
     */
    @Bean
    @StepScope
    public JdbcPagingItemReader<Transaction> partitionedTransactionItemReader(
            @Value("#{stepExecutionContext[partitionStart]}") String partitionStart,
            @Value("#{stepExecutionContext[partitionEnd]}") String partitionEnd,
            @Value("${batch.chunk-size:500}") int chunkSize) throws Exception { // Ambil dari application.yml

        SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
        factory.setDataSource(dataSource);
        factory.setSelectClause("id, transaction_date, reference_number, amount, account_number, status, partition_date");
        factory.setFromClause("from transactions");
        factory.setWhereClause("where partition_date between :partitionStart::date and :partitionEnd::date");

        Map<String, org.springframework.batch.item.database.Order> sortKeys = new HashMap<>();
        sortKeys.put("id", org.springframework.batch.item.database.Order.ASCENDING);
        factory.setSortKeys(sortKeys);

        PagingQueryProvider queryProvider = factory.getObject();

        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("partitionStart", LocalDate.parse(partitionStart));
        parameterValues.put("partitionEnd", LocalDate.parse(partitionEnd));

        return new JdbcPagingItemReaderBuilder<Transaction>()
                .name("partitionedTransactionItemReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                .parameterValues(parameterValues) // Gunakan parameterValues()
                .rowMapper(new TransactionRowMapper())
                .pageSize(chunkSize) // pageSize harus sama dengan chunk size
                .build();
    }

    /**
     * Reader untuk Multi-threaded Step. Menggunakan JdbcPagingItemReader.
     * Parameter startDate dan endDate opsional, bisa digunakan untuk filter data keseluruhan job.
     * @param startDate Tanggal mulai filter (dari JobParameters)
     * @param endDate Tanggal akhir filter (dari JobParameters)
     * @param chunkSize Ukuran chunk dari konfigurasi aplikasi
     * @return JdbcPagingItemReader
     * @throws Exception jika ada masalah dalam membuat PagingQueryProvider
     */
    @Bean
    @StepScope
    public JdbcPagingItemReader<Transaction> multiThreadedTransactionItemReader(
            @Value("#{jobParameters['startDate'] ?: ''}") String startDate, // Opsional
            @Value("#{jobParameters['endDate'] ?: ''}") String endDate,     // Opsional
            @Value("${batch.chunk-size:500}") int chunkSize) throws Exception { // Ambil dari application.yml

        SqlPagingQueryProviderFactoryBean factory = new SqlPagingQueryProviderFactoryBean();
        factory.setDataSource(dataSource);
        factory.setSelectClause("id, transaction_date, reference_number, amount, account_number, status, partition_date");
        factory.setFromClause("from transactions");

        // Tambahkan WHERE clause jika job parameters startDate dan endDate diberikan
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            factory.setWhereClause("where partition_date between :startDate::date and :endDate::date");
        }

        Map<String, org.springframework.batch.item.database.Order> sortKeys = new HashMap<>();
        sortKeys.put("id", org.springframework.batch.item.database.Order.ASCENDING); // Order by primary key for consistent paging
        factory.setSortKeys(sortKeys);

        PagingQueryProvider queryProvider = factory.getObject();

        JdbcPagingItemReaderBuilder<Transaction> builder = new JdbcPagingItemReaderBuilder<Transaction>()
                .name("multiThreadedTransactionItemReader")
                .dataSource(dataSource)
                .queryProvider(queryProvider)
                .rowMapper(new TransactionRowMapper())
                .pageSize(chunkSize); // pageSize harus sama dengan chunk size untuk efisiensi

        // Set parameter kueri jika WHERE clause digunakan
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            Map<String, Object> parameterValues = new HashMap<>();
            parameterValues.put("startDate", LocalDate.parse(startDate));
            parameterValues.put("endDate", LocalDate.parse(endDate));
            builder.parameterValues(parameterValues);
        }

        return builder.build();
    }
}