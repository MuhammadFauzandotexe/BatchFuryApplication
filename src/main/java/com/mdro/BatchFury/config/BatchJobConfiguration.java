//package com.mdro.BatchFury.config;
//
//import com.mdro.BatchFury.step.DataProcessingStepConfig;
//import com.mdro.BatchFury.step.FileProcessingStepConfig;
//import com.mdro.BatchFury.step.ReportGenerationStepConfig;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.JobExecutionListener;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Slf4j
//@Configuration
//@RequiredArgsConstructor
//public class BatchJobConfiguration {
//
//    private final JobRepository jobRepository;
//    private final DataProcessingStepConfig dataProcessingStepConfig;
//    private final FileProcessingStepConfig fileProcessingStepConfig;
//    private final ReportGenerationStepConfig reportGenerationStepConfig;
//
//    @Bean("user-import-job")
//    public Job userImportJob() {
//        return new JobBuilder("user-import-job", jobRepository)
//                .listener(new JobExecutionListener() {
//                    @Override
//                    public void beforeJob(JobExecution jobExecution) {
//                        log.info("Starting User Import Job");
//                    }
//
//                    @Override
//                    public void afterJob(JobExecution jobExecution) {
//                        log.info("User Import Job completed with status: {}", jobExecution.getStatus());
//                    }
//                })
//                .start(dataProcessingStepConfig.dataProcessingStep())
//                .next(fileProcessingStepConfig.fileProcessingStep())
//                .build();
//    }
//
//    @Bean("data-migration-job")
//    public Job dataMigrationJob() {
//        return new JobBuilder("data-migration-job", jobRepository)
//                .listener(new JobExecutionListener() {
//                    @Override
//                    public void beforeJob(JobExecution jobExecution) {
//                        log.info("Starting Data Migration Job");
//                    }
//
//                    @Override
//                    public void afterJob(JobExecution jobExecution) {
//                        log.info("Data Migration Job completed with status: {}", jobExecution.getStatus());
//                    }
//                })
//                .start(dataProcessingStepConfig.dataProcessingStep())
//                .next(reportGenerationStepConfig.reportGenerationStep())
//                .build();
//    }
////
////    @Bean("report-generation-job")
////    public Job reportGenerationJob() {
////        return new JobBuilder("report-generation-job", jobRepository)
////                .listener(new JobExecutionListener() {
////                    @Override
////                    public void beforeJob(JobExecution jobExecution) {
////                        log.info("Starting Report Generation Job");
////                    }
////
////                    @Override
////                    public void afterJob(JobExecution jobExecution) {
////                        log.info("Report Generation Job completed with status: {}", jobExecution.getStatus());
////                    }
////                })
////                .start(reportGenerationStepConfig.reportGenerationStep())
////                .build();
////    }
//}
