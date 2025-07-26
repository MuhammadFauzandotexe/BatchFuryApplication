package com.mdro.BatchFury.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        logger.info("!!! JOB STARTED !!!");
        logger.info("Job Name: {}", jobExecution.getJobInstance().getJobName());
        logger.info("Job ID: {}", jobExecution.getJobId());
        logger.info("Start Time: {}", jobExecution.getStartTime());
        logger.info("Job Parameters: {}", jobExecution.getJobParameters().getParameters());
        logger.info("====================");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            logger.info("!!! JOB FINISHED! Time taken: {} seconds !!!",
                    (Objects.requireNonNull(jobExecution.getEndTime()).compareTo(Objects.requireNonNull(jobExecution.getStartTime())) ) / 1000.0);
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            logger.error("!!! JOB FAILED !!!");
            logger.error("Job Name: {}", jobExecution.getJobInstance().getJobName());
            logger.error("Job ID: {}", jobExecution.getJobId());
            logger.error("Exit Status: {}", jobExecution.getExitStatus().getExitCode());
            jobExecution.getAllFailureExceptions().forEach(e -> logger.error("Failure: {}", e.getMessage()));
        } else {
            logger.warn("!!! JOB COMPLETED WITH STATUS: {} !!!", jobExecution.getStatus());
        }
        logger.info("End Time: {}", jobExecution.getEndTime());
        logger.info("====================");
    }
}