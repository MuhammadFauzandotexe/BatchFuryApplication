package com.mdro.BatchFury.config;

import com.mdro.BatchFury.step.ReportGenerationStepConfig;
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
@Profile("report-generation") // Hanya aktif saat profile 'report-generation' diaktifkan
public class ReportGenerationJobConfig {

    private final JobRepository jobRepository;
    private final ReportGenerationStepConfig reportGenerationStepConfig;

    @Bean("report-generation-job")
    public Job reportGenerationJob() {
        return new JobBuilder("report-generation-job", jobRepository)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("Starting Report Generation Job");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("Report Generation Job completed with status: {}", jobExecution.getStatus());
                    }
                })
                .start(reportGenerationStepConfig.reportGenerationStep())
                .build();
    }
}