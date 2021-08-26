package com.mikuac.shiro.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created on 2021/8/12.
 *
 * @author Zero
 */
@Data
@Component
@ConfigurationProperties(prefix = "shiro.task-pool")
public class TaskPoolProperties {

    /**
     * 线程池名前缀
     */
    private String threadNamePrefix = "ShiroTaskPool-";

    /**
     * 核心线程数（默认线程数）
     */
    private int corePoolSize = 10;

    /**
     * 最大线程数
     */
    private int maxPoolSize = 30;

    /**
     * 允许线程空闲时间（单位：默认为秒）
     */
    private int keepAliveTime = 10;

    /**
     * 缓冲队列大小
     */
    private int queueCapacity = 200;

}
