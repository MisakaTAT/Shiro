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

}
