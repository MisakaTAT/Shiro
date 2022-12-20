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
 * @version $Id: $Id
 */
@Configuration
public class ShiroTaskPoolConfig {

    @Resource
    private TaskPoolProperties taskPoolProperties;

    /**
     * 线程池配置
     *
     * @return {@link ThreadPoolTaskExecutor}
     */
    @Bean("shiroTaskExecutor")
    public ThreadPoolTaskExecutor shiroTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
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
