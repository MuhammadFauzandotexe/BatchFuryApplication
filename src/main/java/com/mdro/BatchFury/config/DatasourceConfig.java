package com.mdro.BatchFury.config;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {

    // ✅ Primary DataSource (Database Remote B) for Spring Batch Metadata
    @Primary
    @Bean(name = "dataSource") // Spring Batch expects this bean name!
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource batchMetadataDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    // ✅ DataSource A - PostgreSQL Local untuk Job A
    @Bean(name = "localDataSource")
    @ConfigurationProperties(prefix = "datasource.local")
    public DataSource localDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    // ✅ DataSource B - PostgreSQL Remote untuk Job B (data transaksi)
    @Bean(name = "remoteDataSource")
    @ConfigurationProperties(prefix = "datasource.remote")
    public DataSource remoteDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "localJdbcTemplate")
    public JdbcTemplate localJdbcTemplate(@Qualifier("localDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "remoteJdbcTemplate")
    public JdbcTemplate remoteJdbcTemplate(@Qualifier("remoteDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "batchJdbcTemplate")
    public JdbcTemplate batchJdbcTemplate(@Qualifier("dataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}

