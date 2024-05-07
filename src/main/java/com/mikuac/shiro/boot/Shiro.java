package com.mikuac.shiro.boot;

import com.mikuac.shiro.adapter.WebSocketClientHandler;
import com.mikuac.shiro.adapter.WebSocketServerHandler;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.handler.EventHandler;
import com.mikuac.shiro.properties.WebSocketProperties;
import com.mikuac.shiro.properties.WebSocketServerProperties;
import com.mikuac.shiro.task.ShiroAsyncTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * Created on 2021/7/16.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Configuration
public class Shiro {

    private WebSocketServerProperties wsServerProp;

    @Autowired
    public void setWebSocketServerProperties(WebSocketServerProperties wsServerProp) {
        this.wsServerProp = wsServerProp;
    }

    private WebSocketProperties wsProp;

    @Autowired
    public void setWebSocketProperties(WebSocketProperties wsProp) {
        this.wsProp = wsProp;
    }

    private BotFactory botFactory;

    @Autowired
    public void setBotFactory(BotFactory botFactory) {
        this.botFactory = botFactory;
    }

    private EventHandler eventHandler;

    @Autowired
    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    private ActionHandler actionHandler;

    @Autowired
    public void setActionHandler(ActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    private ShiroAsyncTask shiroAsyncTask;

    @Autowired
    public void setShiroAsyncTask(ShiroAsyncTask shiroAsyncTask) {
        this.shiroAsyncTask = shiroAsyncTask;
    }

    private BotContainer botContainer;

    @Autowired
    public void setBotContainer(BotContainer botContainer) {
        this.botContainer = botContainer;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "shiro.ws.server.enable", havingValue = "true")
    public WebSocketServerHandler webSocketServerHandler() {
        return new WebSocketServerHandler(eventHandler, botFactory, actionHandler, shiroAsyncTask, botContainer);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "shiro.ws.client.enable", havingValue = "true")
    public WebSocketClientHandler webSocketClientHandler() {
        return new WebSocketClientHandler(eventHandler, botFactory, actionHandler, shiroAsyncTask, botContainer);
    }

    /**
     * <p>createWebSocketContainer.</p>
     *
     * @return {@link ServletServerContainerFactoryBean}
     */
    @Bean
    @ConditionalOnMissingBean
    public ServletServerContainerFactoryBean createWebSocketServerContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(wsProp.getMaxTextMessageBufferSize());
        container.setMaxBinaryMessageBufferSize(wsProp.getMaxBinaryMessageBufferSize());
        container.setMaxSessionIdleTimeout(wsServerProp.getMaxSessionIdleTimeout());
        return container;
    }

}
