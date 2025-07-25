//package com.mdro.BatchFury.controller;
//
//import com.mdro.BatchFury.partitioner.FilteredDailyPartitioner;
//import com.mdro.BatchFury.partitioner.SingleDayPartitioner;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeParseException;
//
//@RestController
//@RequestMapping("/batch")
//public class JobLauncherController {
//
//    private static final Logger logger = LoggerFactory.getLogger(JobLauncherController.class);
//
//    @Autowired
//    private JobLauncher jobLauncher;
//
//    @Autowired
//    @Qualifier("multiThreadedJob")
//    private Job multiThreadedJob;
//
//    @Autowired
//    @Qualifier("partitionedJob")
//    private Job partitionedJob;
//
//    @Autowired
//    @Qualifier("filteredPartitionedJob")
//    private Job filteredPartitionedJob;
//
//    @Autowired
//    @Qualifier("singleDayJob")
//    private Job singleDayJob;
//
//    @Autowired
//    @Qualifier("filteredDailyPartitioner")
//    private FilteredDailyPartitioner filteredDailyPartitioner;
//
//    @Autowired
//    @Qualifier("singleDayPartitioner")
//    private SingleDayPartitioner singleDayPartitioner;
//
//    /**
//     * Endpoint to run the multi-threaded step job.
//     * Example: GET /batch/run-multithreaded
//     */
//    @GetMapping("/run-multithreaded")
//    public String runMultiThreadedJob() throws Exception {
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addLong("time", System.currentTimeMillis())
//                .toJobParameters();
//
//        try {
//            jobLauncher.run(multiThreadedJob, jobParameters);
//            logger.info("Multi-threaded batch job launched successfully");
//            return "Multi-threaded batch job launched!";
//        } catch (Exception e) {
//            logger.error("Failed to launch multi-threaded batch job", e);
//            throw e;
//        }
//    }
//
//    /**
//     * Endpoint to run the partitioned job (processes all available days).
//     * Example: GET /batch/run-partitioned
//     */
//    @GetMapping("/run-partitioned")
//    public String runPartitionedJob() throws Exception {
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addLong("time", System.currentTimeMillis())
//                .toJobParameters();
//
//        try {
//            jobLauncher.run(partitionedJob, jobParameters);
//            logger.info("Partitioned batch job (all days) launched successfully");
//            return "Partitioned batch job (all days) launched!";
//        } catch (Exception e) {
//            logger.error("Failed to launch partitioned batch job (all days)", e);
//            throw e;
//        }
//    }
//
//    /**
//     * Endpoint to run the partitioned job for a specific date range.
//     * Example: GET /batch/run-filtered-partitioned?startDate=2024-01-01&endDate=2024-01-31
//     */
//    @GetMapping("/run-filtered-partitioned")
//    public String runFilteredPartitionedJob(
//            @RequestParam String startDate,
//            @RequestParam String endDate) throws Exception {
//
//        try {
//            LocalDate start = LocalDate.parse(startDate);
//            LocalDate end = LocalDate.parse(endDate);
//
//            filteredDailyPartitioner.setStartDate(startDate);
//            filteredDailyPartitioner.setEndDate(endDate);
//
//            JobParameters jobParameters = new JobParametersBuilder()
//                    .addString("startDate", startDate)
//                    .addString("endDate", endDate)
//                    .addLong("time", System.currentTimeMillis())
//                    .toJobParameters();
//
//            jobLauncher.run(filteredPartitionedJob, jobParameters);
//            logger.info("Filtered partitioned batch job for range {}-{} launched successfully", startDate, endDate);
//            return "Filtered partitioned batch job launched for range " + startDate + " to " + endDate + "!";
//        } catch (DateTimeParseException e) {
//            logger.error("Invalid date format. Please use yyyy-MM-dd.", e);
//            return "Error: Invalid date format. Please use yyyy-MM-dd.";
//        } catch (Exception e) {
//            logger.error("Failed to launch filtered partitioned batch job", e);
//            throw e;
//        }
//    }
//
//    /**
//     * Endpoint to run the partitioned job for a single specific day.
//     * Example: GET /batch/run-single-day-partitioned?targetDate=2024-01-15
//     */
//    @GetMapping("/run-single-day-partitioned")
//    public String runSingleDayJob(
//            @RequestParam String targetDate) throws Exception {
//
//        try {
//            LocalDate day = LocalDate.parse(targetDate);
//
//            singleDayPartitioner.setTargetDate(targetDate);
//
//            JobParameters jobParameters = new JobParametersBuilder()
//                    .addString("targetDate", targetDate)
//                    .addLong("time", System.currentTimeMillis())
//                    .toJobParameters();
//
//            jobLauncher.run(singleDayJob, jobParameters);
//            logger.info("Single day partitioned batch job for {} launched successfully", targetDate);
//            return "Single day partitioned batch job launched for " + targetDate + "!";
//        } catch (DateTimeParseException e) {
//            logger.error("Invalid date format. Please use yyyy-MM-dd.", e);
//            return "Error: Invalid date format. Please use yyyy-MM-dd.";
//        } catch (Exception e) {
//            logger.error("Failed to launch single day partitioned batch job", e);
//            throw e;
//        }
//    }
//}
//
