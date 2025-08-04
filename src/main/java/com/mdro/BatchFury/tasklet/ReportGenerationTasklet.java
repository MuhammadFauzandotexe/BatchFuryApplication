package com.mdro.BatchFury.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReportGenerationTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("Executing Report Generation Tasklet");

        // Simulasi proses report generation
        log.info("Collecting data for report...");
        Thread.sleep(1000); // Simulasi processing time

        log.info("Generating report content...");
        Thread.sleep(2000);

        log.info("Formatting and saving report...");
        Thread.sleep(800);

        log.info("Report generation completed successfully");
        return RepeatStatus.FINISHED;
    }
}