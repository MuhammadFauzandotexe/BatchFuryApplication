package com.mdro.BatchFury.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileProcessingTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("Executing File Processing Tasklet");

        // Simulasi proses file processing
        log.info("Reading files from input directory...");
        Thread.sleep(1500); // Simulasi processing time

        log.info("Processing file content...");
        Thread.sleep(1000);

        log.info("Writing processed files to output directory...");
        Thread.sleep(500);

        log.info("File processing completed successfully");
        return RepeatStatus.FINISHED;
    }
}