package com.mikuac.shiro.task;

import com.mikuac.shiro.properties.TaskPoolProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池
 *
 * @author zero
 */
@Configuration
public class ShiroTaskPoolConfig {

    @Resource
    private TaskPoolProperties taskPoolProperties;

    /**
     * 线程池配置
     *
     * @return executor
     */
    @Bean("shiroTaskExecutor")
    public ThreadPoolTaskExecutor shiroTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskPoolProperties.getCorePoolSize());
        executor.setMaxPoolSize(taskPoolProperties.getMaxPoolSize());
        executor.setQueueCapacity(taskPoolProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(taskPoolProperties.getKeepAliveTime());
        executor.setThreadNamePrefix(taskPoolProperties.getThreadNamePrefix());
        // 当线程池的任务缓存队列已满并且线程池中的线程数目达到maximumPoolSize，如果还有任务到来就会采取任务拒绝策略
        // 通常有以下四种策略：
        // ThreadPoolExecutor.AbortPolicy:丢弃任务并抛出RejectedExecutionException异常。
        // ThreadPoolExecutor.DiscardPolicy：也是丢弃任务，但是不抛出异常。
        // ThreadPoolExecutor.DiscardOldestPolicy：丢弃队列最前面的任务，然后重新尝试执行任务（重复此过程）
        // ThreadPoolExecutor.CallerRunsPolicy：重试添加当前的任务，自动重复调用 execute() 方法，直到成功
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 初始化
        executor.initialize();
        return executor;
    }

}
