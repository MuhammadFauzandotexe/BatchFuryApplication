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

	private static final Logger logger = LoggerFactory.getLogger(BatchFuryApplication.class);

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier("reportGenerationJob") // Nama job yang akan kita definisikan
	private Job reportGenerationJob;


	public static void main(String[] args) {
		SpringApplication.run(BatchFuryApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("Aplikasi Spring Batch dimulai dalam mode standalone.");

		// Tentukan rentang tanggal untuk laporan
		// Ganti dengan tanggal yang Anda tahu ada datanya di DB Anda
		LocalDate startDate = LocalDate.of(2024, 1, 1); // Contoh: 1 Januari 2024
		LocalDate endDate = LocalDate.of(2024, 1, 31);   // Contoh: 31 Januari 2024

		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis()) // Parameter unik untuk setiap eksekusi job
				.addString("startDate", startDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
				.addString("endDate", endDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
				.toJobParameters();

		try {
			logger.info("Meluncurkan reportGenerationJob untuk tanggal {} hingga {}", startDate, endDate);
			jobLauncher.run(reportGenerationJob, jobParameters);
			logger.info("reportGenerationJob selesai. Aplikasi akan mati.");
		} catch (Exception e) {
			logger.error("reportGenerationJob gagal", e);
		} finally {
			System.exit(0); // Penting: Keluar dari aplikasi setelah job selesai atau gagal
		}
	}
}
