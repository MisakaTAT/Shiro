package com.mikuac.shiro.core;

import com.mikuac.shiro.dto.event.message.MessageEvent;
import org.springframework.stereotype.Component;

@Component
public class BotMessageEventInterceptor {

    public boolean preHandle(Bot bot, MessageEvent event) throws Exception {
        return true;
    }

    public void afterCompletion(Bot bot, MessageEvent event) throws Exception {
    }


}
