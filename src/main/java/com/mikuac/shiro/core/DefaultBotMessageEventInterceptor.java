package com.mikuac.shiro.core;

import com.mikuac.shiro.dto.event.message.MessageEvent;
import org.springframework.stereotype.Component;

/**
 * <p>DefaultBotMessageEventInterceptor class.</p>
 *
 * @author Zhongren233
 * @version $Id: $Id
 */
@Component
public class DefaultBotMessageEventInterceptor implements BotMessageEventInterceptor {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(Bot bot, MessageEvent event) {
        // do something...
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterCompletion(Bot bot, MessageEvent event) {
        // do something...
    }

}
