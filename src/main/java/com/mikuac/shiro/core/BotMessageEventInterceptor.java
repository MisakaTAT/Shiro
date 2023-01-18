package com.mikuac.shiro.core;

import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.exception.ShiroException;

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
     * @param event {@link MessageEvent}
     * @return true 为执行 false 为拦截 拦截后不再传递给 plugin
     * @throws ShiroException 异常
     */
    boolean preHandle(Bot bot, MessageEvent event) throws ShiroException;

    /**
     * 执行后
     *
     * @param bot   {@link Bot}
     * @param event {@link MessageEvent}
     * @throws ShiroException 异常
     */
    void afterCompletion(Bot bot, MessageEvent event) throws ShiroException;

}
