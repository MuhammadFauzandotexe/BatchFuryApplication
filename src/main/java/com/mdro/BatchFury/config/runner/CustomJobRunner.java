package com.mdro.BatchFury.config.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class CustomJobRunner implements ApplicationRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRepository jobRepository;

    // Inject semua job yang tersedia
    @Autowired
    private Map<String, Job> jobs;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("CustomJobRunner started with arguments: {}", Arrays.toString(args.getSourceArgs()));

        // Cek apakah ada job names yang dispesifikasi
        List<String> jobNames = args.getOptionValues("spring.batch.job.names");

        if (jobNames == null || jobNames.isEmpty()) {
            log.info("No job names specified. Available jobs: {}", jobs.keySet());
            return;
        }

        for (String jobName : jobNames) {
            executeJob(jobName, args);
        }
    }

    private void executeJob(String jobName, ApplicationArguments args) throws Exception {
        log.info("Attempting to execute job: {}", jobName);

        Job job = jobs.get(jobName);
        if (job == null) {
            log.error("Job '{}' not found. Available jobs: {}", jobName, jobs.keySet());
            return;
        }

        try {
            JobParameters jobParameters = createJobParameters(args);
            log.info("Executing job '{}' with parameters: {}", jobName, jobParameters);

            JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            log.info("Job '{}' executed with status: {}", jobName, jobExecution.getStatus());
            log.info("Job execution ID: {}", jobExecution.getId());

            // Wait untuk job completion jika async
            while (jobExecution.isRunning()) {
                log.info("Job '{}' is still running...", jobName);
                Thread.sleep(1000);
                jobExecution = jobExplorer.getJobExecution(jobExecution.getId());
            }

            BatchStatus finalStatus = jobExecution.getStatus();
            log.info("Job '{}' completed with final status: {}", jobName, finalStatus);

            if (finalStatus == BatchStatus.FAILED) {
                log.error("Job '{}' failed. Exit description: {}", jobName, jobExecution.getExitStatus().getExitDescription());
                // Print stack traces dari step failures
                for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
                    if (stepExecution.getStatus() == BatchStatus.FAILED) {
                        log.error("Step '{}' failed: {}", stepExecution.getStepName(), stepExecution.getExitStatus().getExitDescription());
                        for (Throwable failureException : stepExecution.getFailureExceptions()) {
                            log.error("Step failure exception:", failureException);
                        }
                    }
                }
            }

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
    }

    private JobParameters createJobParameters(ApplicationArguments args) {
        JobParametersBuilder builder = new JobParametersBuilder();

        // Tambahkan timestamp untuk memastikan job bisa dijalankan berulang
        builder.addLong("timestamp", System.currentTimeMillis());

        // Tambahkan parameter dari command line arguments jika ada
        for (String optionName : args.getOptionNames()) {
            if (!optionName.equals("spring.batch.job.names")) {
                List<String> values = args.getOptionValues(optionName);
                if (values != null && !values.isEmpty()) {
                    builder.addString(optionName, String.join(",", values));
                }
            }
        }

        return builder.toJobParameters();
    }
}