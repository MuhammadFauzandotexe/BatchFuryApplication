package com.mdro.BatchFury.reader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public class CustomReader implements ItemReader<String> {
    @Value("#{jobParameters['inputFile']}")
    private String inputFile;

    @Override
    public String read() {
        log.info("Reading from: {}", inputFile);
        return inputFile;
    }
}