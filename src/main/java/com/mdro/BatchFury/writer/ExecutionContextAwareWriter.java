package com.mdro.BatchFury.writer;

import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemWriter;


public class ExecutionContextAwareWriter<T> implements ItemStreamWriter<T>, ItemStream {

    private final FlatFileItemWriter<T> delegate;

    public ExecutionContextAwareWriter(FlatFileItemWriter<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void write(Chunk<? extends T> items) throws Exception {
        delegate.write(items);
    }

    @Override
    public void open(ExecutionContext executionContext) {
        delegate.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) {
        delegate.update(executionContext);
    }

    @Override
    public void close() {
        delegate.close();
    }
}
