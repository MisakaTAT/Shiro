package com.mikuac.shiro.handler.event;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.bo.ArrayMsg;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.GuildMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.handler.Handler;
import com.mikuac.shiro.handler.injection.InjectionHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zero
 */
@Component
public class MessageHandler {

    @Resource
    private Handler handler;

    @Resource
    private InjectionHandler injection;

    public void friend(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        PrivateMessageEvent event = eventJson.to(PrivateMessageEvent.class);
        if (handler.setInterceptor(bot, event)) {
            return;
        }
        event.setArrayMsg(handler.setAnyMessageEvent(bot, eventJson, event));
        injection.invokePrivateMessage(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onPrivateMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }

        try {
            handler.getInterceptor(bot.getBotMessageEventInterceptor()).afterCompletion(bot, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void group(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupMessageEvent event = eventJson.to(GroupMessageEvent.class);
        if (handler.setInterceptor(bot, event)) {
            return;
        }
        event.setArrayMsg(handler.setAnyMessageEvent(bot, eventJson, event));
        injection.invokeGroupMessage(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onGroupMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }

        try {
            handler.getInterceptor(bot.getBotMessageEventInterceptor()).afterCompletion(bot, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void guild(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GuildMessageEvent event = eventJson.to(GuildMessageEvent.class);
        if (handler.setInterceptor(bot, event)) {
            return;
        }
        List<ArrayMsg> arrayMsg = ShiroUtils.stringToMsgChain(event.getMessage());
        event.setArrayMsg(arrayMsg);
        injection.invokeGuildMessage(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onGuildMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }

        try {
            handler.getInterceptor(bot.getBotMessageEventInterceptor()).afterCompletion(bot, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
