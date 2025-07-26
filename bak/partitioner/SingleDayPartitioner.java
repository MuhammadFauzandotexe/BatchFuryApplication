package com.mdro.BatchFury.partitioner;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component("singleDayPartitioner")
public class SingleDayPartitioner implements Partitioner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String targetDate; // Format: "yyyy-MM-dd"

    public SingleDayPartitioner() {}

    public SingleDayPartitioner(String targetDate) {
        this.targetDate = targetDate;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> result = new HashMap<>();

        if (targetDate == null) {
            throw new IllegalArgumentException("Target date must be specified (format: yyyy-MM-dd)");
        }

        LocalDate day = LocalDate.parse(targetDate);

        // Check if data exists for this day
        String checkSql = "SELECT COUNT(*) FROM transactions WHERE partition_date = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, day);

        if (count > 0) {
            ExecutionContext executionContext = new ExecutionContext();
            executionContext.putString("partitionStart", day.format(DateTimeFormatter.ISO_LOCAL_DATE));
            executionContext.putString("partitionEnd", day.format(DateTimeFormatter.ISO_LOCAL_DATE));
            executionContext.putString("partitionDay", day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            result.put("partition0", executionContext);
        }

        return result;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }
}