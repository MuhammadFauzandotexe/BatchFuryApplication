package com.mdro.BatchFury.partitioner;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DailyPartitioner implements Partitioner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> result = new HashMap<>();

        // Get distinct days from database
        // This query assumes 'partition_date' is a DATE column
        String sql = "SELECT DISTINCT partition_date FROM transactions ORDER BY partition_date";

        List<LocalDate> days = jdbcTemplate.queryForList(sql, LocalDate.class);

        for (int i = 0; i < days.size(); i++) {
            ExecutionContext executionContext = new ExecutionContext();
            LocalDate day = days.get(i);

            executionContext.putString("partitionStart", day.format(DateTimeFormatter.ISO_LOCAL_DATE));
            executionContext.putString("partitionEnd", day.format(DateTimeFormatter.ISO_LOCAL_DATE));
            executionContext.putString("partitionDay", day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

            result.put("partition" + i, executionContext);
        }

        return result;
    }
}
