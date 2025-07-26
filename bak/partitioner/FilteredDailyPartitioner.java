package com.mdro.BatchFury.partitioner;


import lombok.Setter;
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

@Component("filteredDailyPartitioner")
public class FilteredDailyPartitioner implements Partitioner {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Setter
    private String startDate; // Format: "yyyy-MM-dd"
    @Setter
    private String endDate;   // Format: "yyyy-MM-dd"

    public FilteredDailyPartitioner() {}

    public FilteredDailyPartitioner(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> result = new HashMap<>();

        String sql;
        List<LocalDate> days;

        if (startDate != null && endDate != null) {
            sql = "SELECT DISTINCT partition_date FROM transactions " +
                    "WHERE partition_date BETWEEN ?::date AND ?::date " +
                    "ORDER BY partition_date";

            days = jdbcTemplate.queryForList(sql, LocalDate.class,
                    LocalDate.parse(startDate), LocalDate.parse(endDate));
        } else {
            // Fallback to all days if range not specified
            sql = "SELECT DISTINCT partition_date FROM transactions ORDER BY partition_date";
            days = jdbcTemplate.queryForList(sql, LocalDate.class);
        }

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