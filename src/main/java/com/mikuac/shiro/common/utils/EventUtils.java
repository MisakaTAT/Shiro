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

import java.util.Collections;
import java.util.List;

/**
 * @author zero
 * @version $Id: $Id
 */
@Slf4j
@Component
public class EventUtils {

    private ApplicationContext ctx;

    @Autowired
    public void setCtx(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    private InjectionHandler injection;

    @Autowired
    public void setInjection(InjectionHandler injection) {
        this.injection = injection;
    }

    private final BotPlugin defaultPlugin = new BotPlugin();

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
            log.warn("Plugin {} skip, Please check @Component annotation", pluginClass.getSimpleName());
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
            log.warn("Interceptor {} skip, Please check @Component annotation", interceptorClass.getSimpleName());
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
            log.error("Set interceptor exception: {}", e.getMessage(), e);
            return true;
        }
    }

    /**
     * 推送消息
     *
     * @param bot      {@link Bot}
     * @param resp     {@link JSONObject}
     * @return {@link ArrayMsg}
     */
    private List<ArrayMsg> pushAnyMessageEvent(Bot bot, JSONObject resp) {
        AnyMessageEvent event = resp.to(AnyMessageEvent.class);
        List<ArrayMsg> arrayMsg = ShiroUtils.rawToArrayMsg(event.getMessage(),event);
        event.setArrayMsg(arrayMsg);
        injection.invokeAnyMessage(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (getPlugin(pluginClass).onAnyMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
        return arrayMsg;
    }

    /**
     * 推送消息并返回消息链
     *
     * @param bot   {@link Bot}
     * @param resp  {@link JSONObject}
     * @param event {@link MessageEvent}
     * @return {@link ArrayMsg}
     */
    public List<ArrayMsg> setAnyMessageEvent(Bot bot, JSONObject resp, MessageEvent event) {
        try {
            return pushAnyMessageEvent(bot, resp);
        } catch (Exception e) {
            log.error("Push any message event exception: {}", e.getMessage(), e);
        }
        return Collections.emptyList();
    }

}
