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
@ConfigurationProperties(prefix = "shiro.ws.client")
public class WebSocketClientProperties {

    /**
     * 是否启用正向 Websocket 连接
     */
    private Boolean enable = false;

    /**
     * ws地址
     */
    private String url = "";

    /**
     * 断线重连时间（秒）
     */
    private Integer reconnectInterval = 5;

}
