package com.mikuac.shiro.core;

import com.mikuac.shiro.dto.event.message.MessageEvent;

/**
 * MessageEvent Interceptor
 *
 * @author Zhongren233
 * @version $Id: $Id
 */
public interface BotMessageEventInterceptor {

    /**
     * 预处理
     *
     * @param bot   {@link Bot}
     * @param event {@link com.mikuac.shiro.dto.event.message.GroupMessageEvent}
     *              {@link com.mikuac.shiro.dto.event.message.GuildMessageEvent}
     *              {@link com.mikuac.shiro.dto.event.message.PrivateMessageEvent}
     * @return true 为执行 false 为拦截 拦截后不再传递给 plugin
     * @throws Exception 异常
     */
    boolean preHandle(Bot bot, MessageEvent event) throws Exception;

    /**
     * 执行后
     *
     * @param bot   {@link Bot}
     * @param event {@link com.mikuac.shiro.dto.event.message.GroupMessageEvent}
     *              {@link com.mikuac.shiro.dto.event.message.GuildMessageEvent}
     *              {@link com.mikuac.shiro.dto.event.message.PrivateMessageEvent}
     * @throws Exception 异常
     */
    void afterCompletion(Bot bot, MessageEvent event) throws Exception;

}
