package com.mikuac.shiro.core;

import com.mikuac.shiro.dto.event.message.MessageEvent;
import org.springframework.stereotype.Component;

/**
 * MessageEvent拦截器
 */
@Component
public class BotMessageEventInterceptor {
    /**
     * 预执行
     *
     * @param bot 收到信息事件的bot
     * @param event 信息事件 可能为{#{@link com.mikuac.shiro.dto.event.message.PrivateMessageEvent}}或{#{@link com.mikuac.shiro.dto.event.message.GroupMessageEvent}}
     *
     * @return true为执行 false为拦截 拦截后不再传递给plugin
     * */
    public boolean preHandle(Bot bot, MessageEvent event) throws Exception {
        return true;
    }
    /**
     * 执行后
     *
     * @param bot 收到信息事件的bot
     * @param event 信息事件 可能为{#{@link com.mikuac.shiro.dto.event.message.PrivateMessageEvent}}或{#{@link com.mikuac.shiro.dto.event.message.GroupMessageEvent}}
     * */
    public void afterCompletion(Bot bot, MessageEvent event) throws Exception {
    }


}
