package com.mikuac.shiro.handler.event;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.request.FriendAddRequestEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import com.mikuac.shiro.handler.Handler;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zero
 */
@Component
public class RequestHandler {

    @Resource
    private Handler handler;

    public void friend(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        FriendAddRequestEvent event = eventJson.to(FriendAddRequestEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onFriendAddRequest(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void group(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupAddRequestEvent event = eventJson.to(GroupAddRequestEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onGroupAddRequest(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

}
