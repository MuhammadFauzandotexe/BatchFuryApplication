package com.mdro.BatchFury.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Component;

@Component
public class RetryInfoListener implements RetryListener {

    private static final Logger logger = LoggerFactory.getLogger(RetryInfoListener.class);

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        logger.info("Retry attempt started for: {}", callback.getClass().getSimpleName());
        return true;
    }

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        logger.warn("Retry attempt {} failed with exception: {}",
                context.getRetryCount(), throwable.getMessage());
    }

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if (throwable == null) {
            logger.info("Retry successful after {} attempts", context.getRetryCount());
        } else {
            logger.error("Retry failed after {} attempts. Final exception: {}",
                    context.getRetryCount(), throwable.getMessage());
        }
    }
}
