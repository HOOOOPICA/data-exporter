package com.hpk.dataexpoter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    // 定义线程池，异步任务用
    @Bean("exportThreadPool")
    public Executor exportThreadPool(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);      // 同时最多2个导出任务
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(10);    // 最多排队10个
        executor.setThreadNamePrefix("export-");
        executor.initialize();
        return executor;
    }
}
