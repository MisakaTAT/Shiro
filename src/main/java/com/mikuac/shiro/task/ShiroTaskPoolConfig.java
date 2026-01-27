package com.mikuac.shiro.task;

import com.mikuac.shiro.properties.TaskPoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.thread.Threading;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池
 *
 * @author zero
 * @version $Id: $Id
 */
@Configuration
public class ShiroTaskPoolConfig {

    private final TaskPoolProperties taskPoolProperties;

    @Autowired
    public ShiroTaskPoolConfig(TaskPoolProperties taskPoolProperties) {
        this.taskPoolProperties = taskPoolProperties;
    }


    /**
     * 平台线程池
     * @return {@link ThreadPoolTaskExecutor}
     */
    @Bean("shiroTaskExecutor")
    @ConditionalOnThreading(Threading.PLATFORM)
    @ConditionalOnProperty(value = "shiro.task-pool.enable-task-pool", havingValue = "true", matchIfMissing = true)
    public ThreadPoolTaskExecutor shiroTaskPlatformExecutor() {
        return getThreadPoolTaskExecutor(Thread.ofPlatform().factory());
    }

    /**
     * 虚拟线程池
     * @return {@link ThreadPoolTaskExecutor}
     */
    @Bean("shiroTaskExecutor")
    @ConditionalOnThreading(Threading.VIRTUAL)
    @ConditionalOnProperty(value = "shiro.task-pool.enable-task-pool", havingValue = "true", matchIfMissing = true)
    public ThreadPoolTaskExecutor shiroTaskVirtualExecutor() {
        return getThreadPoolTaskExecutor(Thread.ofVirtual().factory());
    }


    /**
     * 初始化并获取线程池
     * @param factory 线程工厂 {@link ThreadFactory}
     * @return {@link ThreadPoolTaskExecutor}
     */
    private ThreadPoolTaskExecutor getThreadPoolTaskExecutor(ThreadFactory factory) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadFactory(factory);
        executor.setCorePoolSize(taskPoolProperties.getCorePoolSize());
        executor.setMaxPoolSize(taskPoolProperties.getMaxPoolSize());
        executor.setQueueCapacity(taskPoolProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(taskPoolProperties.getKeepAliveTime());
        executor.setThreadNamePrefix(taskPoolProperties.getThreadNamePrefix());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

}
