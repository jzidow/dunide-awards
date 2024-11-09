package com.ninjaone.dundie_awards.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableRetry
public class AsyncConfig {
    @Value("${dundie.thread-pool.core-pool-size}")
    private int corePoolSize;

    @Value("${dundie.thread-pool.max-pool-size}")
    private int maxPoolSize;

    @Value("${dundie.thread-pool.queue-capacity}")
    private int queueCapacity;

    @Value("${dundie.thread-pool.thread-name-prefix}")
    private String threadNamePrefix;

    @Bean(name = "customTaskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}
