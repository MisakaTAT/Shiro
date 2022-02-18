package com.mikuac.shiro.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created on 2021/8/25.
 *
 * @author Zero
 */
@Data
@Component
@ConfigurationProperties(prefix = "shiro.limiter")
public class RateLimiterProperties {

    /**
     * 是否启用限速器（令牌桶算法）
     */
    private boolean enable = false;

    /**
     * 补充速率（每秒补充的令牌数量）
     */
    private int rate = 1;

    /**
     * 令牌桶容量
     */
    private int capacity = 1;

    /**
     * 如果该值为 true 时，当令牌获取失败则会阻塞当前线程，后续任务将被添加到等待队列。
     * 如果该值为 false 时，当令牌获取失败则会直接丢次本次请求。
     */
    private boolean awaitTask = true;

    /**
     * 等待超时
     */
    private int timeout = 10;

}
