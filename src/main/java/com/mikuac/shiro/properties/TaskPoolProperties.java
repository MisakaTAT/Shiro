package com.mikuac.shiro.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created on 2021/8/12.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
@Component
@ConfigurationProperties(prefix = "shiro.task-pool")
public class TaskPoolProperties {

    /**
     * 是否启用shiro的线程池, 无配置或者 true 为开启, 默认值开启
     */
    private Boolean enableTaskPool = true;

    /**
     * 线程池名前缀
     */
    private String threadNamePrefix = "ShiroTaskPool-";

    /**
     * 核心线程数（默认线程数）
     */
    private Integer corePoolSize = 10;

    /**
     * 最大线程数
     */
    private Integer maxPoolSize = 30;

    /**
     * 允许线程空闲时间（单位：默认为秒）
     */
    private Integer keepAliveTime = 10;

    /**
     * 缓冲队列大小
     */
    private Integer queueCapacity = 200;

}
