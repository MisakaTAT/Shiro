package com.mikuac.shiro.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 反向 ws 配置
 * Created on 2021/7/7.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
@Component
@ConfigurationProperties(prefix = "shiro.ws.server")
public class WebSocketServerProperties {

    /**
     * 是否启用反向 Websocket 连接
     */
    private Boolean enable = false;

    /**
     * ws地址
     */
    private String url = "/ws/shiro";

}
