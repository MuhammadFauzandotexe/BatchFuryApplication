package com.mdro.BatchFury.profile;

import com.mdro.BatchFury.constant.BatchBeanNames;
import com.mdro.BatchFury.constant.DataSourceBeanNames;
import com.mdro.BatchFury.listener.ChunkProgressListener;
import com.mdro.BatchFury.listener.JobCompletionNotificationListener;
import com.mdro.BatchFury.listener.StepExecutionInfoListener;
import com.mdro.BatchFury.model.Transaction;
import com.mdro.BatchFury.processor.TransactionItemProcessor;
import com.mdro.BatchFury.writer.ReportWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
public class ExportLocalTransactionConfig {
}
