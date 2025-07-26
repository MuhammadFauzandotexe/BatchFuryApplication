package com.mdro.BatchFury.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Component
public class ChunkProgressListener implements ChunkListener {

    private static final Logger logger = LoggerFactory.getLogger(ChunkProgressListener.class);

    @Override
    public void beforeChunk(ChunkContext context) {
        // Optional: Log chunk start if needed for debugging
    }

    @Override
    public void afterChunk(ChunkContext context) {
        Long readCount = context.getStepContext().getStepExecution().getReadCount();
        Long writeCount = context.getStepContext().getStepExecution().getWriteCount();
        Long skipCount = context.getStepContext().getStepExecution().getSkipCount();

        logger.info("Chunk Progress - Read: {}, Written: {}, Skipped: {}",
                readCount, writeCount, skipCount);
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        logger.error("Chunk Error occurred in step: {}",
                context.getStepContext().getStepName());
    }
}
