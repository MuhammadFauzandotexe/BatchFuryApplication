package com.mdro.BatchFury.configuration;


import com.mdro.BatchFury.listener.ChunkProgressListener;
import com.mdro.BatchFury.listener.JobCompletionNotificationListener;
import com.mdro.BatchFury.listener.RetryInfoListener;
import com.mdro.BatchFury.listener.StepExecutionInfoListener;

import com.mdro.BatchFury.model.ReportData;
import com.mdro.BatchFury.model.Transaction;
import com.mdro.BatchFury.proccesor.TransactionProcessor;
import com.mdro.BatchFury.reader.TransactionReader;
import com.mdro.BatchFury.writer.ReportWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
// import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler; // Dihilangkan
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Qualifier; // Dihilangkan jika tidak ada Qualifier lain
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;
    // @Autowired // Dihilangkan
    // private DailyPartitioner dailyPartitioner; // Dihilangkan
    // @Autowired // Dihilangkan
    // @Qualifier("filteredDailyPartitioner") // Dihilangkan
    // private FilteredDailyPartitioner filteredDailyPartitioner; // Dihilangkan
    // @Autowired // Dihilangkan
    // @Qualifier("singleDayPartitioner") // Dihilangkan
    // private SingleDayPartitioner singleDayPartitioner; // Dihilangkan

    @Autowired
    private StepExecutionInfoListener stepExecutionInfoListener;
    @Autowired
    private ChunkProgressListener chunkProgressListener;
    @Autowired
    private RetryInfoListener retryInfoListener;
    @Autowired
    private JobCompletionNotificationListener jobCompletionNotificationListener;

    @Autowired
    private TransactionReader transactionReaderComponent;

    // ========================= TASK EXECUTORS =========================
    @Bean
    @Primary // Mark as primary for general use
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("single-process-batch-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    // ========================= COMMON ITEM COMPONENTS =========================
    @Bean
    public TransactionProcessor transactionProcessor() {
        return new TransactionProcessor();
    }

    @Bean
    public ReportWriter reportWriter() {
        return new ReportWriter();
    }

    // ========================= 1. Multi-threaded Step (Single Process) =========================
    @Bean
    public Step multiThreadedStep() throws Exception {
        return new StepBuilder("multiThreadedStep", jobRepository)
                .<Transaction, ReportData>chunk(500, transactionManager)
                .reader(transactionReaderComponent.multiThreadedTransactionItemReader("", "", 500))
                .processor(transactionProcessor())
                .writer(reportWriter().reportFileWriter("multi-threaded-report")) // Single output file for this mode
                .taskExecutor(taskExecutor()) // Use the general taskExecutor
                .listener(stepExecutionInfoListener)
                .listener(chunkProgressListener)
                .faultTolerant()
                .skipLimit(100)
                .skip(DataIntegrityViolationException.class)
                .retryLimit(3)
                .retry(DeadlockLoserDataAccessException.class)
                .listener(retryInfoListener)
                .build();
    }

    // ========================= 2. Parallel Steps (Single Process) =========================
    @Bean
    public Step generateHeaderStep() {
        return new StepBuilder("generateHeaderStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Generating Report Header in " + Thread.currentThread().getName());
                    Thread.sleep(500);
                    return null;
                }, transactionManager)
                .listener(stepExecutionInfoListener)
                .build();
    }

    @Bean
    public Step generateBodyStep() {
        return new StepBuilder("generateBodyStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Generating Report Body in " + Thread.currentThread().getName());
                    Thread.sleep(2000);
                    return null;
                }, transactionManager)
                .listener(stepExecutionInfoListener)
                .build();
    }

    @Bean
    public Step generateFooterStep() {
        return new StepBuilder("generateFooterStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Generating Report Footer in " + Thread.currentThread().getName());
                    Thread.sleep(500);
                    return null;
                }, transactionManager)
                .listener(stepExecutionInfoListener)
                .build();
    }

    @Bean
    public Job parallelStepsJob() {
        Flow flow1 = new FlowBuilder<Flow>("flow1")
                .start(generateHeaderStep())
                .build();

        Flow flow2 = new FlowBuilder<Flow>("flow2")
                .start(generateBodyStep())
                .build();

        Flow flow3 = new FlowBuilder<Flow>("flow3")
                .start(generateFooterStep())
                .build();

        return new JobBuilder("parallelStepsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(new FlowBuilder<Flow>("splitFlow")
                        .split(taskExecutor())
                        .add(flow1, flow2, flow3)
                        .build())
                .end()
                .listener(jobCompletionNotificationListener)
                .build();
    }

    @Bean
    public Job multiThreadedJob() throws Exception {
        return new JobBuilder("multiThreadedJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(multiThreadedStep())
                .listener(jobCompletionNotificationListener)
                .build();
    }
}