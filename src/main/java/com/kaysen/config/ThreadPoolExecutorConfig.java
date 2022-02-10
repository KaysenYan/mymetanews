package com.kaysen.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 线程池配置
 */
@Configuration //spring配置类标志注解
@EnableAsync //开启异步支持，在需要使用多线程的方法上加@Async注解
public class ThreadPoolExecutorConfig implements AsyncConfigurer {
    @Value("${newsapp.my-executor.core-pool-size}")
    private int COREPOOLSIZE; //核心线程数
    @Value("${newsapp.my-executor.max-pool-size}")
    private int MAXPOOLSIZE; //最大线程数
    @Value("${newsapp.my-executor.queue-capacity}")
    private int QUEUECAPACITY; //等待队列容量

    /**
     * @return 自定义的线程池
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //设置核心线程数
        executor.setCorePoolSize(COREPOOLSIZE);
        //设置最大线程数
        executor.setMaxPoolSize(MAXPOOLSIZE);
        //设置等待队列，等待队列满了才会开临时线程
        executor.setQueueCapacity(QUEUECAPACITY);
        //设置线程名前缀
        executor.setThreadNamePrefix("MyExecutor-");
        //初始化线程池
        executor.initialize();
        //线程池交给spring管理
        return executor;
    }

    /**
     * @return 多线程异常处理器
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
