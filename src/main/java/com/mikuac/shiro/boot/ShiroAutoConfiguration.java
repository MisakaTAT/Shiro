package com.mikuac.shiro.boot;

import com.mikuac.shiro.handler.WebSocketHandler;
import com.mikuac.shiro.properties.WebSocketProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

/**
 * Created on 2021/7/15.
 *
 * @author Zero
 */
@Configuration
@ComponentScan(
        basePackages = {"com.mikuac.shiro"}
)
@EnableWebSocket
@EnableAsync
@Import(Shiro.class)
public class ShiroAutoConfiguration implements WebSocketConfigurer {

    @Resource
    private WebSocketProperties webSocketProperties;

    @Resource
    private WebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, webSocketProperties.getWsUrl()).setAllowedOrigins("*");
    }

}
