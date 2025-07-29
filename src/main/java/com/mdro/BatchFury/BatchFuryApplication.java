package com.mdro.BatchFury;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing; // HAPUS IMPORT INI

@SpringBootApplication
// HAPUS ANOTASI @EnableBatchProcessing DARI SINI
public class BatchFuryApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchFuryApplication.class, args);
	}
}
