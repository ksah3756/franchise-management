package com.goorm.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync(proxyTargetClass = true)
@Configuration
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    // @Async는 기본적으로 SimpleAsyncTaskExecutor를 사용하는데, 이는 매번 새로운 쓰레드를 생성하기 때문에 비효율적이므로, ThreadPoolTaskExecutor를 사용한다.
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("customAsyncExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("Exception occurred while async task: {}", ex.getMessage());
            log.error("method name: {}", method.getName());
            for (Object param : params) {
                log.error("parameter value: {}", param);
            }
        };
    }
}
