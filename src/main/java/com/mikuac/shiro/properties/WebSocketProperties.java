package com.mikuac.shiro.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Objects;

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
     * 认证约定
     */
    private String authSchema = "Bearer";

    /**
     * 是否启用约定
     */
    private Boolean enableAuthSchema = true;

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

    /**
     * 获取访问密钥
     *
     * @return 密钥
     */
    public String getAccessToken() {
        if (Boolean.TRUE.equals(enableAuthSchema) && !Objects.equals(accessToken, "")) {
            return authSchema + " " + accessToken;
        }
        if (Boolean.TRUE.equals(enableAuthSchema) && Objects.equals(accessToken, "")) {
            return "";
        }
        return accessToken;
    }

}
