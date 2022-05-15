package com.mikuac.shiro.core;

import com.mikuac.shiro.dto.event.message.MessageEvent;

/**
 * MessageEvent Interceptor
 *
 * @author Zhongren233
 */
@SuppressWarnings("all")
public interface BotMessageEventInterceptor {

    /**
     * 预执行
     *
     * @param bot   收到信息事件的 bot
     * @param event 信息事件，可能为：
     *              {@link com.mikuac.shiro.dto.event.message.PrivateMessageEvent}
     *              {@link com.mikuac.shiro.dto.event.message.GroupMessageEvent}
     *              {@link com.mikuac.shiro.dto.event.message.GuildMessageEvent}
     * @return true 为执行 false 为拦截 拦截后不再传递给 plugin
     * @throws Exception 任何异常
     */
    boolean preHandle(Bot bot, MessageEvent event) throws Exception;

    /**
     * 执行后
     *
     * @param bot   收到信息事件的 bot
     * @param event 信息事件，可能为：
     *              {@link com.mikuac.shiro.dto.event.message.PrivateMessageEvent}
     *              {@link com.mikuac.shiro.dto.event.message.GroupMessageEvent}
     *              {@link com.mikuac.shiro.dto.event.message.GuildMessageEvent}
     * @throws Exception 任何异常
     */
    void afterCompletion(Bot bot, MessageEvent event) throws Exception;

}
