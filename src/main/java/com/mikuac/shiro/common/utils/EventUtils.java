package com.mikuac.shiro.common.utils;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotMessageEventInterceptor;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.core.DefaultBotMessageEventInterceptor;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.handler.injection.InjectionHandler;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zero
 * @version $Id: $Id
 */
@Slf4j
@Component
public class EventUtils {

    private final BotPlugin defaultPlugin = new BotPlugin();
    private ApplicationContext ctx;
    private InjectionHandler injection;

    @Autowired
    public void setCtx(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Autowired
    public void setInjection(InjectionHandler injection) {
        this.injection = injection;
    }

    /**
     * 获取插件
     *
     * @param pluginClass {@link Class}
     * @return {@link BotPlugin}
     */
    public BotPlugin getPlugin(Class<? extends BotPlugin> pluginClass) {
        try {
            return ctx.getBean(pluginClass);
        } catch (Exception e) {
            log.warn("Plugin {} is skipped. Please check the @Component annotation.", pluginClass.getSimpleName());
            return defaultPlugin;
        }
    }

    /**
     * 获取拦截器
     *
     * @param interceptorClass {@link Class}
     * @return {@link BotMessageEventInterceptor}
     */
    public BotMessageEventInterceptor getInterceptor(Class<? extends BotMessageEventInterceptor> interceptorClass) {
        try {
            return ctx.getBean(interceptorClass);
        } catch (Exception e) {
            log.warn("Interceptor {} is skipped. Please check the @Component annotation.", interceptorClass.getSimpleName());
            return ctx.getBean(DefaultBotMessageEventInterceptor.class);
        }
    }

    /**
     * 设置拦截器
     *
     * @param bot   {@link Bot}
     * @param event {@link MessageEvent}
     * @return boolean
     */
    public boolean setInterceptor(Bot bot, MessageEvent event) {
        try {
            return !getInterceptor(bot.getBotMessageEventInterceptor()).preHandle(bot, event);
        } catch (Exception e) {
            log.error("Interceptor set exception: {}", e.getMessage(), e);
            return true;
        }
    }

    /**
     * 推送消息
     *
     * @param bot      {@link Bot}
     * @param resp     {@link JSONObject}
     * @param arrayMsg {@link ArrayMsg}
     */
    public void pushAnyMessageEvent(Bot bot, JSONObject resp, List<ArrayMsg> arrayMsg) {
        try {
            AnyMessageEvent event = resp.to(AnyMessageEvent.class);
            event.setArrayMsg(arrayMsg);
            injection.invokeAnyMessage(bot, event);
            for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                if (getPlugin(pluginClass).onAnyMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("An exception occurred while pushing the message event: {}", e.getMessage(), e);
        }
    }

}
