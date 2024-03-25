package com.mikuac.shiro.boot;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.mikuac.shiro.adapter.WebSocketClient;
import com.mikuac.shiro.adapter.WebSocketServer;
import com.mikuac.shiro.properties.ShiroProperties;
import com.mikuac.shiro.properties.WebSocketClientProperties;
import com.mikuac.shiro.properties.WebSocketProperties;
import com.mikuac.shiro.properties.WebSocketServerProperties;
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

    private WebSocketServerProperties wsServerProp;

    @Autowired
    public void setWebSocketServerProperties(WebSocketServerProperties wsServerProp) {
        this.wsServerProp = wsServerProp;
    }

    private WebSocketClientProperties wsClientProp;

    @Autowired
    public void setWebSocketClientProperties(WebSocketClientProperties wsClientProp) {
        this.wsClientProp = wsClientProp;
    }

    private WebSocketProperties wsProp;

    @Autowired
    public void setWebSocketProperties(WebSocketProperties wsProp) {
        this.wsProp = wsProp;
    }

    private ShiroProperties shiroProperties;

    @Autowired
    public void setShiroProperties(ShiroProperties shiroProperties) {
        this.shiroProperties = shiroProperties;
        WebSocketServer.setWaitWebsocketConnect(shiroProperties.getWaitBotConnect());
    }

    private WebSocketServer wsServer;

    @Autowired(required = false)
    public void setWebSocketServer(WebSocketServer webSocketServer) {
        this.wsServer = webSocketServer;
    }

    private WebSocketClient wsClient;

    @Autowired(required = false)
    public void setWebSocketClient(WebSocketClient webSocketClient) {
        this.wsClient = webSocketClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerWebSocketHandlers(@Nonnull WebSocketHandlerRegistry registry) {
        setLogLevel();
        if (Boolean.TRUE.equals(wsServerProp.getEnable()) && Boolean.TRUE.equals(wsClientProp.getEnable())) {
            log.error("Cannot be simultaneously enabled ws client and server");
            System.exit(0);
            return;
        }
        if (Boolean.TRUE.equals(wsServerProp.getEnable())) {
            registry.addHandler(wsServer, wsServerProp.getUrl()).setAllowedOrigins("*");
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
            headers.add("Authorization", "Bearer " + wsProp.getAccessToken());
        }
        WebSocketConnectionManager manager = new WebSocketConnectionManager(client, wsClient, wsClientProp.getUrl());
        manager.setHeaders(headers);
        manager.start();
    }

}
