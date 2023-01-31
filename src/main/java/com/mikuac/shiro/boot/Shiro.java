package com.mikuac.shiro.boot;

import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.handler.EventHandler;
import com.mikuac.shiro.handler.WebSocketHandler;
import com.mikuac.shiro.properties.WebSocketProperties;
import com.mikuac.shiro.task.ShiroAsyncTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

    private WebSocketProperties webSocketProperties;

    @Autowired
    public void setWebSocketProperties(WebSocketProperties webSocketProperties) {
        this.webSocketProperties = webSocketProperties;
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

    /**
     * <p>createShiroWebSocketHandler.</p>
     *
     * @return {@link WebSocketHandler}
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSocketHandler createShiroWebSocketHandler() {
        return new WebSocketHandler(eventHandler, botFactory, actionHandler, shiroAsyncTask, botContainer);
    }

    /**
     * <p>createWebSocketContainer.</p>
     *
     * @return {@link ServletServerContainerFactoryBean}
     */
    @Bean
    @ConditionalOnMissingBean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(webSocketProperties.getMaxTextMessageBufferSize());
        container.setMaxBinaryMessageBufferSize(webSocketProperties.getMaxBinaryMessageBufferSize());
        container.setMaxSessionIdleTimeout(webSocketProperties.getMaxSessionIdleTimeout());
        return container;
    }

}
