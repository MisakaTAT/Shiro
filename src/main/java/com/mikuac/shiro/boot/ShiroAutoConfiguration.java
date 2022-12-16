package com.mikuac.shiro.boot;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.mikuac.shiro.handler.WebSocketHandler;
import com.mikuac.shiro.properties.ShiroProperties;
import com.mikuac.shiro.properties.WebSocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

/**
 * Created on 2021/7/15.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Slf4j
@Component
@EnableAsync
@EnableWebSocket
@Import(Shiro.class)
@ComponentScan("com.mikuac.shiro")
public class ShiroAutoConfiguration implements WebSocketConfigurer {

    @Resource
    private WebSocketProperties webSocketProperties;

    @Resource
    private ShiroProperties shiroProperties;

    @Resource
    private WebSocketHandler webSocketHandler;

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        setLogLevel();
        registry.addHandler(webSocketHandler, webSocketProperties.getWsUrl()).setAllowedOrigins("*");
    }

    private void setLogLevel() {
        if (shiroProperties.isDebug()) {
            Logger pkg = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.mikuac.shiro");
            pkg.setLevel(Level.DEBUG);
            log.warn("Enabled debug mode");
        }
    }

}
