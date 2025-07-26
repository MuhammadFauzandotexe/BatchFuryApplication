package com.mdro.BatchFury.config;

import com.mdro.BatchFury.constant.BatchBeanNames;
import com.mdro.BatchFury.constant.DataSourceBeanNames;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
@Configuration
@EnableBatchProcessing
public class MetadataJobConfig {

    private final DataSource metadataDataSource;

    public MetadataJobConfig(@Qualifier(DataSourceBeanNames.METADATA) DataSource metadataDataSource) {
        this.metadataDataSource = metadataDataSource;
    }

    @Bean(name = BatchBeanNames.TX_MANAGER)
    public PlatformTransactionManager batchTransactionManager() {
        return new DataSourceTransactionManager(metadataDataSource);
    }

    @Bean(name = BatchBeanNames.JOB_REPOSITORY)
    public JobRepository jobRepository(
            @Qualifier(BatchBeanNames.TX_MANAGER) PlatformTransactionManager txManager
    ) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(metadataDataSource);
        factory.setTransactionManager(txManager);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean(name = BatchBeanNames.JOB_LAUNCHER)
    public JobLauncher jobLauncher(
            @Qualifier(BatchBeanNames.JOB_REPOSITORY) JobRepository jobRepository
    ) throws Exception {
        SimpleJobLauncher launcher = new SimpleJobLauncher();
        launcher.setJobRepository(jobRepository);
        launcher.afterPropertiesSet();
        return launcher;
    }

    @Bean(name = BatchBeanNames.JOB_EXPLORER)
    public JobExplorer jobExplorer() throws Exception {
        JobExplorerFactoryBean factory = new JobExplorerFactoryBean();
        factory.setDataSource(metadataDataSource);
        factory.setTransactionManager(batchTransactionManager());
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}