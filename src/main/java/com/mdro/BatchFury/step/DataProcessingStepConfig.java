package com.mdro.BatchFury.step;


import com.mdro.BatchFury.tasklet.DataProcessingTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class DataProcessingStepConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataProcessingTasklet dataProcessingTasklet;

    @Bean
    public Step dataProcessingStep() {
        return new StepBuilder("dataProcessingStep", jobRepository)
                .tasklet(dataProcessingTasklet, transactionManager)
                .build();
    }
}