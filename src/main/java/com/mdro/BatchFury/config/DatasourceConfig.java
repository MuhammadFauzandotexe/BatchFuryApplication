package com.mdro.BatchFury.config;

import com.mdro.BatchFury.constant.DataSourceBeanNames;
import com.mdro.BatchFury.constant.JdbcTemplateBeanNames;
import com.mdro.BatchFury.constant.TransactionManagerBeanNames;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {

    // ========================== METADATA (Batch) ===============================
    @Primary
    @Bean(name = DataSourceBeanNames.METADATA)
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource metadataDataSource() {
        return createDataSource();
    }

    @Bean(name = TransactionManagerBeanNames.METADATA)
    public PlatformTransactionManager metadataTransactionManager(
            @Qualifier(DataSourceBeanNames.METADATA) DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = JdbcTemplateBeanNames.METADATA)
    public JdbcTemplate metadataJdbcTemplate(
            @Qualifier(DataSourceBeanNames.METADATA) DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // ========================== LOCAL DATABASE ================================
    @Bean(name = DataSourceBeanNames.LOCAL)
    @ConfigurationProperties(prefix = "datasource.local")
    public DataSource localDataSource() {
        return createDataSource();
    }

    @Bean(name = TransactionManagerBeanNames.LOCAL)
    public PlatformTransactionManager localTransactionManager(
            @Qualifier(DataSourceBeanNames.LOCAL) DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = JdbcTemplateBeanNames.LOCAL)
    public JdbcTemplate localJdbcTemplate(
            @Qualifier(DataSourceBeanNames.LOCAL) DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // ========================== REMOTE DATABASE ===============================
    @Bean(name = DataSourceBeanNames.REMOTE)
    @ConfigurationProperties(prefix = "datasource.remote")
    public DataSource remoteDataSource() {
        return createDataSource();
    }

    @Bean(name = TransactionManagerBeanNames.REMOTE)
    public PlatformTransactionManager remoteTransactionManager(
            @Qualifier(DataSourceBeanNames.REMOTE) DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = JdbcTemplateBeanNames.REMOTE)
    public JdbcTemplate remoteJdbcTemplate(
            @Qualifier(DataSourceBeanNames.REMOTE) DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // ========================== HELPER METHOD ================================
    private DataSource createDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }
}
