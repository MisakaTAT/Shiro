package com.mikuac.shiro.handler.event;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.common.utils.EventUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.request.FriendAddRequestEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author zero
 */
@Component
public class RequestEvent {

    @Resource
    private EventUtils utils;

    /**
     * 存储请求事件处理器
     */
    public final Map<String, BiConsumer<Bot, JSONObject>> handlers = new HashMap<>();

    /**
     * 请求事件分发
     *
     * @param bot       {@link Bot}
     * @param eventJson {@link JSONObject}
     */
    public void handler(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        String type = eventJson.getString("request_type");
        handlers.getOrDefault(
                type,
                (b, e) -> {
                }
        ).accept(bot, eventJson);
    }

    public void friend(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        FriendAddRequestEvent event = eventJson.to(FriendAddRequestEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onFriendAddRequest(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void group(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupAddRequestEvent event = eventJson.to(GroupAddRequestEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onGroupAddRequest(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

}
