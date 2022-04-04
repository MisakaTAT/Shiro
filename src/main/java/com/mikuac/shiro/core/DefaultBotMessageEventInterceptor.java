package com.mikuac.shiro.core;

import com.mikuac.shiro.dto.event.message.MessageEvent;
import org.springframework.stereotype.Component;

/**
 * @author Zhongren233
 */
@Component
public class DefaultBotMessageEventInterceptor implements BotMessageEventInterceptor {

    @Override
    public boolean preHandle(Bot bot, MessageEvent event) {
        return true;
    }

    @Override
    public void afterCompletion(Bot bot, MessageEvent event) {
    }

}
