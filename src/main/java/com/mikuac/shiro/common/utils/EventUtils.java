package com.mikuac.shiro.common.utils;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.bo.ArrayMsg;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotMessageEventInterceptor;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.core.DefaultBotMessageEventInterceptor;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.handler.injection.InjectionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zero
 * @version $Id: $Id
 */
@Slf4j
@Component
public class EventUtils {

    @Resource
    private ApplicationContext ctx;

    @Resource
    private InjectionHandler injection;

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
            e.printStackTrace();
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
    private void pushAnyMessageEvent(Bot bot, JSONObject resp, List<ArrayMsg> arrayMsg) {
        AnyMessageEvent event = resp.to(AnyMessageEvent.class);
        event.setArrayMsg(arrayMsg);
        injection.invokeAnyMessage(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (getPlugin(pluginClass).onAnyMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
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
            List<ArrayMsg> arrayMsg = ShiroUtils.rawToArrayMsg(event.getMessage());
            pushAnyMessageEvent(bot, resp, arrayMsg);
            return arrayMsg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
