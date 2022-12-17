package com.mikuac.shiro.handler.event;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.bo.ArrayMsg;
import com.mikuac.shiro.common.utils.EventUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.GuildMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.handler.injection.InjectionHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author zero
 */
@Component
public class MessageEvent {

    @Resource
    private EventUtils utils;

    @Resource
    private InjectionHandler injection;

    /**
     * 存储消息事件处理器
     */
    public final Map<String, BiConsumer<Bot, JSONObject>> handlers = new HashMap<>();

    /**
     * 消息事件分发
     *
     * @param bot       {@link Bot}
     * @param eventJson {@link JSONObject}
     */
    public void handler(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        String type = eventJson.getString("message_type");
        handlers.getOrDefault(
                type,
                (b, e) -> {
                }
        ).accept(bot, eventJson);
    }

    public void friend(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        PrivateMessageEvent event = eventJson.to(PrivateMessageEvent.class);
        if (utils.setInterceptor(bot, event)) {
            return;
        }
        event.setArrayMsg(utils.setAnyMessageEvent(bot, eventJson, event));
        injection.invokePrivateMessage(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onPrivateMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }

        try {
            utils.getInterceptor(bot.getBotMessageEventInterceptor()).afterCompletion(bot, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void group(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupMessageEvent event = eventJson.to(GroupMessageEvent.class);
        if (utils.setInterceptor(bot, event)) {
            return;
        }
        event.setArrayMsg(utils.setAnyMessageEvent(bot, eventJson, event));
        injection.invokeGroupMessage(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onGroupMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }

        try {
            utils.getInterceptor(bot.getBotMessageEventInterceptor()).afterCompletion(bot, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void guild(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GuildMessageEvent event = eventJson.to(GuildMessageEvent.class);
        if (utils.setInterceptor(bot, event)) {
            return;
        }
        List<ArrayMsg> arrayMsg = ShiroUtils.stringToMsgChain(event.getMessage());
        event.setArrayMsg(arrayMsg);
        injection.invokeGuildMessage(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onGuildMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }

        try {
            utils.getInterceptor(bot.getBotMessageEventInterceptor()).afterCompletion(bot, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
