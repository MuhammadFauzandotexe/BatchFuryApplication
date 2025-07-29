package com.mdro.BatchFury.config.runner;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class Runner3 implements ApplicationRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private Map<String, Job> jobs;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("CustomJobRunner started with arguments: {}", Arrays.toString(args.getSourceArgs()));

        List<String> jobNames = args.getOptionValues("spring.batch.job.names");
        boolean continueOnError = args.containsOption("continue-on-error") &&
                args.getOptionValues("continue-on-error").stream().anyMatch("true"::equalsIgnoreCase);
        boolean failFast = args.containsOption("fail-fast") &&
                args.getOptionValues("fail-fast").stream().anyMatch("true"::equalsIgnoreCase);

        if (jobNames == null || jobNames.isEmpty()) {
            log.info("No job names specified. Available jobs: {}", jobs.keySet());
            return;
        }

        for (String jobName : jobNames) {
            boolean success = executeJob(jobName, args, failFast);
            if (!success && !continueOnError) {
                log.warn("Job '{}' failed. Aborting remaining jobs (use --continue-on-error=true to override)", jobName);
                break;
            }
        }
    }

    private boolean executeJob(String jobName, ApplicationArguments args, boolean failFast) throws Exception {
        log.info("Attempting to execute job: {}", jobName);

        Job job = jobs.get(jobName);
        if (job == null) {
            log.error("Job '{}' not found. Available jobs: {}", jobName, jobs.keySet());
            return false;
        }

        try {
            JobParameters jobParameters = createJobParameters(args);
            log.info("Executing job '{}' with parameters: {}", jobName, jobParameters);

            JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            while (jobExecution.isRunning()) {
                log.info("Job '{}' is still running...", jobName);
                Thread.sleep(1000);
                jobExecution = jobExplorer.getJobExecution(jobExecution.getId());

                if (failFast) {
                    for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
                        if (stepExecution.getStatus() == BatchStatus.FAILED) {
                            log.warn("Fail-fast mode active. Cancelling job '{}'...", jobName);
                            jobExecution.upgradeStatus(BatchStatus.STOPPING);
                            return false;
                        }
                    }
                }
            }

            BatchStatus finalStatus = jobExecution.getStatus();
            log.info("Job '{}' completed with final status: {}", jobName, finalStatus);

            if (finalStatus == BatchStatus.COMPLETED) {
                return true;
            }

            log.error("Job '{}' failed or not completed successfully. Exit description: {}",
                    jobName, jobExecution.getExitStatus().getExitDescription());

            for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
                if (stepExecution.getStatus() == BatchStatus.FAILED) {
                    log.error("Step '{}' failed: {}", stepExecution.getStepName(), stepExecution.getExitStatus().getExitDescription());
                    for (Throwable failureException : stepExecution.getFailureExceptions()) {
                        log.error("Step failure exception:", failureException);
                    }
                }
            }

            // Dummy email notification
            sendFailureNotification(jobName, finalStatus);

        } catch (JobExecutionAlreadyRunningException e) {
            log.error("Job '{}' is already running", jobName, e);
        } catch (JobRestartException e) {
            log.error("Job '{}' restart exception", jobName, e);
        } catch (JobInstanceAlreadyCompleteException e) {
            log.error("Job '{}' instance already complete", jobName, e);
        } catch (JobParametersInvalidException e) {
            log.error("Invalid job parameters for job '{}': {}", jobName, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error executing job '{}'", jobName, e);
        }

        return false;
    }

    private JobParameters createJobParameters(ApplicationArguments args) {
        JobParametersBuilder builder = new JobParametersBuilder();
        builder.addLong("timestamp", System.currentTimeMillis());

        for (String optionName : args.getOptionNames()) {
            if (!Set.of("spring.batch.job.names", "continue-on-error", "fail-fast").contains(optionName)) {
                List<String> values = args.getOptionValues(optionName);
                if (values != null && !values.isEmpty()) {
                    builder.addString(optionName, String.join(",", values));
                }
            }
        }

        return builder.toJobParameters();
    }

    private void sendFailureNotification(String jobName, BatchStatus status) {
        log.info("📧 Sending failure notification for job '{}'. Final status: {}", jobName, status);
        // TODO: Integrasi ke email API / notification system
    }
}
