package com.mikuac.shiro.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created on 2021/7/7.
 *
 * @author Zero
 */
@Data
@Component
@ConfigurationProperties(prefix = "shiro.ws-config")
public class WebSocketProperties {

    /**
     * 超时回收，10秒
     */
    private Long doRequestTimeout = 10 * 1000L;

    /**
     * ws地址
     */
    private String wsUrl = "/ws/shiro";

    /**
     * 最大文本消息缓冲区
     */
    private Integer maxTextMessageBufferSize = 512000;

    /**
     * 二进制消息的最大长度
     */
    private Integer maxBinaryMessageBufferSize = 512000;

    /**
     * 最大空闲时间，超过这个时间将close session
     */
    private Long maxSessionIdleTimeout = 15 * 60000L;

}
