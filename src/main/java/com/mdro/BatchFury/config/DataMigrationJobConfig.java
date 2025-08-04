package com.mdro.BatchFury.config;

import com.mdro.BatchFury.step.DataProcessingStepConfig;
import com.mdro.BatchFury.step.ReportGenerationStepConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("data-migration") // Hanya aktif saat profile 'data-migration' diaktifkan
public class DataMigrationJobConfig {

    private final JobRepository jobRepository;
    private final DataProcessingStepConfig dataProcessingStepConfig;
    private final ReportGenerationStepConfig reportGenerationStepConfig;

    @Bean("data-migration-job")
    public Job dataMigrationJob() {
        return new JobBuilder("data-migration-job", jobRepository)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        JobParameters params = jobExecution.getJobParameters();
                        String inputFile = params.getString("inputFile");
                        log.info("File input: {}", inputFile);
                        log.info("Starting Data Migration Job");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        JobParameters params = jobExecution.getJobParameters();
                        String inputFile = params.getString("inputFile");
                        log.info("File input: {}", inputFile);
                        log.info("Data Migration Job completed with status: {}", jobExecution.getStatus());
                    }
                })
                .start(dataProcessingStepConfig.dataProcessingStep())
                .next(reportGenerationStepConfig.reportGenerationStep())
                .build();
    }
}