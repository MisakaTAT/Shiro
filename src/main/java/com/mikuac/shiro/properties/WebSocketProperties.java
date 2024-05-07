package com.mikuac.shiro.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 正向 ws 配置
 *
 * @author Zero
 */
@Data
@Component
@ConfigurationProperties(prefix = "shiro.ws")
public class WebSocketProperties {

    /**
     * 访问密钥, 强烈推荐在公网的服务器设置
     */
    private String accessToken = "";

    /**
     * 超时回收，10秒
     */
    private Integer timeout = 10;

    /**
     * 最大文本消息缓冲区 512KB
     */
    private Integer maxTextMessageBufferSize = 512000;

    /**
     * 二进制消息的最大长度 512KB
     */
    private Integer maxBinaryMessageBufferSize = 512000;

}
