package com.mikuac.shiro.boot;

import com.mikuac.shiro.adapter.WebSocketClientHandler;
import com.mikuac.shiro.adapter.WebSocketServerHandler;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.core.CoreEvent;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.handler.EventHandler;
import com.mikuac.shiro.properties.ShiroProperties;
import com.mikuac.shiro.properties.WebSocketProperties;
import com.mikuac.shiro.task.ScheduledTask;
import com.mikuac.shiro.task.ShiroAsyncTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created on 2021/7/16.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Configuration
public class Shiro {

    private final WebSocketProperties wsProp;
    private final BotFactory botFactory;
    private final EventHandler eventHandler;
    private final ActionHandler actionHandler;
    private final ShiroAsyncTask shiroAsyncTask;
    private final BotContainer botContainer;
    private final CoreEvent coreEvent;
    private final ScheduledTask scheduledTask;
    private final ShiroProperties shiroProps;
    private final ThreadPoolTaskExecutor shiroTaskExecutor;

    @Autowired
    public Shiro(
            WebSocketProperties wsProp, BotFactory botFactory,
            EventHandler eventHandler, ActionHandler actionHandler, ShiroAsyncTask shiroAsyncTask,
            BotContainer botContainer, CoreEvent coreEvent, ScheduledTask scheduledTask, ShiroProperties shiroProps,
            @Qualifier("shiroTaskExecutor") ThreadPoolTaskExecutor shiroTaskExecutor
    ) {
        this.wsProp = wsProp;
        this.botFactory = botFactory;
        this.eventHandler = eventHandler;
        this.actionHandler = actionHandler;
        this.shiroAsyncTask = shiroAsyncTask;
        this.botContainer = botContainer;
        this.coreEvent = coreEvent;
        this.scheduledTask = scheduledTask;
        this.shiroProps = shiroProps;
        this.shiroTaskExecutor = shiroTaskExecutor;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "shiro.ws.server.enable", havingValue = "true")
    public WebSocketServerHandler webSocketServerHandler() {
        return new WebSocketServerHandler(
                eventHandler, botFactory, actionHandler, shiroAsyncTask,
                botContainer, coreEvent, wsProp, scheduledTask, shiroProps,
                shiroTaskExecutor
        );
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "shiro.ws.client.enable", havingValue = "true")
    public WebSocketClientHandler webSocketClientHandler() {
        return new WebSocketClientHandler(
                eventHandler, botFactory, actionHandler, shiroAsyncTask,
                botContainer, coreEvent, wsProp, shiroTaskExecutor
        );
    }

}
