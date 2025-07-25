package com.mdro.BatchFury.writer;

import com.mdro.BatchFury.model.ReportData;
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
            @Value("#{stepExecutionContext[partitionDay]}") String partitionDay) { // Use partitionDay


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
}