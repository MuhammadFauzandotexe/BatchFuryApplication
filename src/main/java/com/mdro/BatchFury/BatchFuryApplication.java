package com.mdro.BatchFury;

import com.mdro.BatchFury.constant.BatchBeanNames;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class BatchFuryApplication implements CommandLineRunner {

	private final JobLauncher jobLauncher;
	private final Job processRemoteTransactionsJob;

	public BatchFuryApplication(
			@Qualifier(BatchBeanNames.JOB_LAUNCHER) JobLauncher jobLauncher,
			@Qualifier("processRemoteTransactionsJob") Job processRemoteTransactionsJob
	) {
		this.jobLauncher = jobLauncher;
		this.processRemoteTransactionsJob = processRemoteTransactionsJob;
	}

	public static void main(String[] args) {
		SpringApplication.run(BatchFuryApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		JobParameters params = new JobParametersBuilder()
				.addLong("timestamp", System.currentTimeMillis())
				.toJobParameters();

		JobExecution execution = jobLauncher.run(processRemoteTransactionsJob, params);
		System.out.println("Exit Status: " + execution.getStatus());
	}
}

