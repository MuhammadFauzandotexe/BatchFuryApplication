package com.mdro.BatchFury.listener;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobStarter {

    private final JobLauncher jobLauncher;
    private final List<Job> jobs;
    private final Environment environment;

    public JobStarter(JobLauncher jobLauncher,
                      List<Job> jobs, // semua job yang didaftarkan sebagai @Bean
                      Environment environment) {
        this.jobLauncher = jobLauncher;
        this.jobs = jobs;
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runJobsBasedOnProfile() {
        String[] activeProfiles = environment.getActiveProfiles();

        for (String profile : activeProfiles) {
            jobs.stream()
                    .filter(job -> job.getName().toLowerCase().contains(profile.toLowerCase()))
                    .forEach(job -> {
                        try {
                            JobParameters jobParameters = new JobParametersBuilder()
                                    .addLong("timestamp", System.currentTimeMillis())
                                    .toJobParameters();
                            JobExecution execution = jobLauncher.run(job, jobParameters);
                            System.out.printf("✅ Job '%s' triggered for profile '%s'%n", job.getName(), profile);
                        } catch (Exception e) {
                            System.err.printf("❌ Failed to run job '%s': %s%n", job.getName(), e.getMessage());
                        }
                    });
        }
    }
}
