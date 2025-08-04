package com.mdro.BatchFury.step;

import com.mdro.BatchFury.tasklet.FileProcessingTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class FileProcessingStepConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final FileProcessingTasklet fileProcessingTasklet;

    @Bean
    public Step fileProcessingStep() {
        return new StepBuilder("fileProcessingStep", jobRepository)
                .tasklet(fileProcessingTasklet, transactionManager)
                .build();
    }
}