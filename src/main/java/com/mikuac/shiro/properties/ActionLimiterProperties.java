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
public class ActionLimiterProperties {

    /**
     * 是否启用限速器（令牌桶算法）
     */
    private boolean enable = false;

    /**
     * 每秒生成的令牌数
     */
    private int permitsPerSecond = 1;

    /**
     * acquire 如果令牌获取失败，将会阻塞当前线程直到获取成功（后面的 action 将会等待处理，不会被丢弃）
     * tryAcquire 如果令牌获取失败，该 action 将被丢弃
     */
    private String mode = "acquire";

}
