package com.mikuac.shiro.boot;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.mikuac.shiro.adapter.WebSocketClientHandler;
import com.mikuac.shiro.adapter.WebSocketServerHandler;
import com.mikuac.shiro.properties.ShiroProperties;
import com.mikuac.shiro.properties.WebSocketClientProperties;
import com.mikuac.shiro.properties.WebSocketProperties;
import com.mikuac.shiro.properties.WebSocketServerProperties;
import com.mikuac.shiro.task.ScheduledTask;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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

    private final WebSocketServerProperties wsServerProp;
    private final WebSocketClientProperties wsClientProp;
    private final WebSocketProperties wsProp;
    private final ScheduledTask scheduledTask;
    private final ShiroProperties shiroProperties;
    private final WebSocketServerHandler webSocketServerHandler;
    private final WebSocketClientHandler webSocketClientHandler;

    @Autowired
    public ShiroAutoConfiguration(
            WebSocketServerProperties wsServerProp,
            WebSocketClientProperties wsClientProp,
            WebSocketProperties wsProp,
            ShiroProperties shiroProperties,
            ScheduledTask scheduledTask,
            Optional<WebSocketServerHandler> webSocketServerHandler,
            Optional<WebSocketClientHandler> webSocketClientHandler
    ) {

        this.wsServerProp = wsServerProp;
        this.wsClientProp = wsClientProp;
        this.scheduledTask = scheduledTask;
        this.wsProp = wsProp;
        this.shiroProperties = shiroProperties;
        this.webSocketServerHandler = webSocketServerHandler.orElse(null);
        this.webSocketClientHandler = webSocketClientHandler.orElse(null);
    }

    @Override
    public void registerWebSocketHandlers(@Nonnull WebSocketHandlerRegistry registry) {
        setLogLevel();
        if (Boolean.TRUE.equals(wsServerProp.getEnable()) && Boolean.TRUE.equals(wsClientProp.getEnable())) {
            log.error("Cannot be simultaneously enabled ws client and server");
            System.exit(0);
            return;
        }
        if (Boolean.TRUE.equals(wsServerProp.getEnable())) {
            registry.addHandler(webSocketServerHandler, wsServerProp.getUrl()).setAllowedOrigins("*");
        }
        if (Boolean.TRUE.equals(wsClientProp.getEnable())) {
            createWebsocketClient();
        }
    }

    private void setLogLevel() {
        if (Boolean.TRUE.equals(shiroProperties.getDebug())) {
            Logger pkg = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.mikuac.shiro");
            pkg.setLevel(Level.DEBUG);
            log.warn("Enabled debug mode");
        }
    }

    private void createWebsocketClient() {
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        if (!Objects.equals(wsProp.getAccessToken(), "")) {
            headers.add("Authorization", wsProp.getAccessToken());
        }
        WebSocketConnectionManager manager = new WebSocketConnectionManager(client, webSocketClientHandler, wsClientProp.getUrl());
        manager.setHeaders(headers);
        manager.setAutoStartup(true);

        scheduledTask.executor().scheduleWithFixedDelay(() -> {
            if (!manager.isConnected()) {
                manager.startInternal();
            }
        }, 0, wsClientProp.getReconnectInterval(), TimeUnit.SECONDS);
    }

}
