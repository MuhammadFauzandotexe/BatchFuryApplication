package com.mdro.BatchFury;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class BatchFuryApplication implements CommandLineRunner {

	@Autowired
	private JobLauncher jobLauncher;

	// Semua job Spring Batch Anda akan terinject di sini secara otomatis
	@Autowired
	private Map<String, Job> jobs;

	public static void main(String[] args) {
		SpringApplication.run(BatchFuryApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String selectedJobName = "processRemoteTransactionsJob"; // Default jika tidak ada --job=

		for (String arg : args) {
			if (arg.startsWith("--job=")) {
				selectedJobName = arg.substring("--job=".length());
			}
		}

		if (!jobs.containsKey(selectedJobName)) {
			System.err.println("❌ Job name tidak ditemukan: " + selectedJobName);
			System.err.println("✅ Daftar job yang tersedia: " + jobs.keySet());
			return;
		}

		Job selectedJob = jobs.get(selectedJobName);

		System.out.println("🚀 Menjalankan job: " + selectedJobName);

		JobParameters params = new JobParametersBuilder()
				.addLong("timestamp", System.currentTimeMillis()) // Supaya bisa rerun
				.toJobParameters();

		jobLauncher.run(selectedJob, params);

		System.out.println("✅ Job selesai dijalankan!");
	}
}
