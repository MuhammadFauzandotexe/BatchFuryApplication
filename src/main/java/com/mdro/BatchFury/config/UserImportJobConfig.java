package com.mdro.BatchFury.config;

import com.mdro.BatchFury.step.DataProcessingStepConfig;
import com.mdro.BatchFury.step.FileProcessingStepConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("user-import") // Hanya aktif saat profile 'user-import' diaktifkan
public class UserImportJobConfig {

    private final JobRepository jobRepository;
    private final DataProcessingStepConfig dataProcessingStepConfig;
    private final FileProcessingStepConfig fileProcessingStepConfig;

    @Bean("user-import-job")
    public Job userImportJob() {
        return new JobBuilder("user-import-job", jobRepository)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("Starting User Import Job");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("User Import Job completed with status: {}", jobExecution.getStatus());
                    }
                })
                .start(dataProcessingStepConfig.dataProcessingStep())
                .next(fileProcessingStepConfig.fileProcessingStep())
                .build();
    }
}