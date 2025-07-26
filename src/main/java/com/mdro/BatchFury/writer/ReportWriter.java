package com.mdro.BatchFury.writer;

import com.mdro.BatchFury.model.ReportData;
import com.mdro.BatchFury.model.Transaction;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public class ReportWriter {

    @Bean
    @StepScope
    public FlatFileItemWriter<ReportData> reportFileWriter(
            @Value("#{stepExecutionContext[partitionDay] ?: jobParameters['reportDate'] ?: 'default'}") String partitionDay) {

        // Output file name will be e.g., reports/daily_report_2024-07-25.txt
        String fileName = String.format("reports/daily_report_%s.txt", partitionDay);

        BeanWrapperFieldExtractor<ReportData> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"referenceNumber", "transactionDate", "amount", "accountNumber", "status"});

        DelimitedLineAggregator<ReportData> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter("|");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<ReportData>()
                .name("reportFileWriter")
                .resource(new FileSystemResource(fileName))
                .lineAggregator(lineAggregator)
                .headerCallback(writer -> writer.write("Reference|Date|Amount|Account|Status"))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<ReportData> simpleReportFileWriter(
            @Value("#{jobParameters['reportDate'] ?: 'default'}") String reportDate) {

        String fileName = String.format("reports/transaction_report_%s.txt", reportDate);

        BeanWrapperFieldExtractor<ReportData> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"referenceNumber", "transactionDate", "amount", "accountNumber", "status"});

        DelimitedLineAggregator<ReportData> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter("|");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<ReportData>()
                .name("simpleReportFileWriter")
                .resource(new FileSystemResource(fileName))
                .lineAggregator(lineAggregator)
                .headerCallback(writer -> writer.write("Reference|Date|Amount|Account|Status"))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Transaction> simpleReportFileWriterFromTransaction(
            @Value("#{jobParameters['reportDate'] ?: 'default'}") String reportDate) {

        String fileName = String.format("reports/transaction_report_%s.txt", reportDate);

        BeanWrapperFieldExtractor<Transaction> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id","referenceNumber", "transactionDate", "amount", "accountNumber", "status"});

        DelimitedLineAggregator<Transaction> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter("|");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<Transaction>()
                .name("simpleReportFileWriter")
                .resource(new FileSystemResource(fileName))
                .lineAggregator(lineAggregator)
                .headerCallback(writer -> writer.write("ID|Reference|Date|Amount|Account|Status"))
                .build();
    }
}