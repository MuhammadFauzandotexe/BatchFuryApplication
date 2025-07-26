package com.mdro.BatchFury.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;
@Component
public class StepExecutionInfoListener implements StepExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(StepExecutionInfoListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.info("=== STEP STARTED ===");
        logger.info("Step Name: {}", stepExecution.getStepName());
        logger.info("Job Name: {}", stepExecution.getJobExecution().getJobInstance().getJobName());
        logger.info("Start Time: {}", stepExecution.getStartTime());
        logger.info("Thread: {}", Thread.currentThread().getName());
        logger.info("=====================");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("=== STEP COMPLETED ===");
        logger.info("Step Name: {}", stepExecution.getStepName());
        logger.info("Status: {}", stepExecution.getStatus());
        logger.info("Exit Code: {}", stepExecution.getExitStatus().getExitCode());
        logger.info("Start Time: {}", stepExecution.getStartTime());
        logger.info("End Time: {}", stepExecution.getEndTime());

        // Data Processing Information
        logger.info("--- DATA PROCESSING SUMMARY ---");
        logger.info("Read Count: {}", stepExecution.getReadCount());
        logger.info("Write Count: {}", stepExecution.getWriteCount());
        logger.info("Commit Count: {}", stepExecution.getCommitCount());
        logger.info("Rollback Count: {}", stepExecution.getRollbackCount());

        // Error Information
        logger.info("--- ERROR SUMMARY ---");
        logger.info("Read Skip Count: {}", stepExecution.getReadSkipCount());
        logger.info("Write Skip Count: {}", stepExecution.getWriteSkipCount());
        logger.info("Process Skip Count: {}", stepExecution.getProcessSkipCount());
        logger.info("Filter Count: {}", stepExecution.getFilterCount());

        // Calculate success and failure rates
        long totalProcessed = stepExecution.getReadCount();
        long totalSkipped;
        totalSkipped = stepExecution.getReadSkipCount() +
                stepExecution.getWriteSkipCount() +
                stepExecution.getProcessSkipCount();
        long totalSuccess = stepExecution.getWriteCount();

        logger.info("--- FINAL STATISTICS ---");
        logger.info("Total Records Processed: {}", totalProcessed);
        logger.info("Successfully Processed: {}", totalSuccess);
        logger.info("Failed/Skipped: {}", totalSkipped);
        logger.info("Success Rate: {}%",
                totalProcessed > 0 ? (totalSuccess * 100.0 / totalProcessed) : 0);

        stepExecution.getFailureExceptions();
        if (!stepExecution.getFailureExceptions().isEmpty()) {
            logger.error("--- FAILURE EXCEPTIONS ---");
            stepExecution.getFailureExceptions().forEach(exception ->
                    logger.error("Exception: {}", exception.getMessage()));
        }

        logger.info("Thread: {}", Thread.currentThread().getName());
        logger.info("=======================");

        return stepExecution.getExitStatus();
    }
}
