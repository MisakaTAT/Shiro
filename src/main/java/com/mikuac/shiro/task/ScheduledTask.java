package com.mikuac.shiro.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;

@Component
public class ScheduledTask {

    private final ThreadPoolTaskExecutor shiroTaskExecutor;

    @Autowired
    public ScheduledTask(ThreadPoolTaskExecutor shiroTaskExecutor) {
        this.shiroTaskExecutor = shiroTaskExecutor;
    }

    private ScheduledThreadPoolExecutor executor;

    public ScheduledThreadPoolExecutor executor() {
        if (executor != null) {
            return executor;
        }
        executor = new ScheduledThreadPoolExecutor(
                shiroTaskExecutor.getCorePoolSize(),
                shiroTaskExecutor.getThreadPoolExecutor().getThreadFactory()
        );
        executor.setRemoveOnCancelPolicy(true);
        return executor;
    }

}