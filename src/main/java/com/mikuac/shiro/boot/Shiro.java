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
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import javax.annotation.Resource;

/**
 * Created on 2021/7/16.
 *
 * @author Zero
 */
@Component
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
     * 创建ShiroWebSocketHandler
     *
     * @return ShiroWebSocketHandler
     */
    @Bean
    @ConditionalOnMissingBean
    public WebSocketHandler createShiroWebSocketHandler() {
        return new WebSocketHandler(eventHandler, botFactory, actionHandler, shiroAsyncTask, botContainer);
    }

    /**
     * 设置ServletServerContainerFactoryBean
     *
     * @return ServletServerContainerFactoryBean
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
