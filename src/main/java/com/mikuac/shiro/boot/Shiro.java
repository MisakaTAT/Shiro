package com.mikuac.shiro.boot;

import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.handler.EventHandler;
import com.mikuac.shiro.handler.WebSocketHandler;
import com.mikuac.shiro.properties.WebSocketProperties;
import com.mikuac.shiro.task.ShiroAsyncTask;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import javax.annotation.Resource;

/**
 * Created on 2021/7/16.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Configuration
public class Shiro {

    @Resource
    private WebSocketProperties webSocketProperties;

    @Resource
    private BotFactory botFactory;

    @Resource
    private EventHandler eventHandler;

    @Resource
    private ActionHandler actionHandler;

    @Resource
    private ShiroAsyncTask shiroAsyncTask;

    @Resource
    private BotContainer botContainer;

    /**
     * <p>createShiroWebSocketHandler.</p>
     *
     * @return {@link com.mikuac.shiro.handler.WebSocketHandler}
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSocketHandler createShiroWebSocketHandler() {
        return new WebSocketHandler(eventHandler, botFactory, actionHandler, shiroAsyncTask, botContainer);
    }

    /**
     * <p>createWebSocketContainer.</p>
     *
     * @return {@link org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean}
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
