package com.mdro.BatchFury;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;


@SpringBootApplication
@EnableBatchProcessing
public class BatchFuryApplication implements CommandLineRunner {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job processTransactionsJob;

	public static void main(String[] args) {
		System.out.println("Starting Spring Batch Application...");
		SpringApplication.run(BatchFuryApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Executing Spring Batch Job...");

		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.toJobParameters();

		jobLauncher.run(processTransactionsJob, jobParameters);

		System.out.println("Spring Batch Job completed!");
	}
}
