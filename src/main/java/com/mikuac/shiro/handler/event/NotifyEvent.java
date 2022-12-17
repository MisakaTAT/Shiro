package com.mikuac.shiro.handler.event;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.common.utils.EventUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.notice.GroupHonorChangeNoticeEvent;
import com.mikuac.shiro.dto.event.notice.GroupLuckyKingNoticeEvent;
import com.mikuac.shiro.dto.event.notice.PokeNoticeEvent;
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
public class NotifyEvent {

    @Resource
    private EventUtils utils;

    /**
     * 存储通知事件处理器
     */
    public final Map<String, BiConsumer<Bot, JSONObject>> handlers = new HashMap<>();

    /**
     * 通知事件分发
     *
     * @param bot       {@link Bot}
     * @param eventJson {@link JSONObject}
     */
    public void handler(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        String type = eventJson.getString("sub_type");
        handlers.getOrDefault(
                type,
                (b, e) -> {
                }
        ).accept(bot, eventJson);
    }

    public void poke(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        PokeNoticeEvent event = eventJson.to(PokeNoticeEvent.class);
        // 如果群号不为空则当作群内戳一戳处理
        if (event.getGroupId() > 0L) {
            for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                if (utils.getPlugin(pluginClass).onGroupPokeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                    break;
                }
            }
            return;
        }
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onPrivatePokeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void luckyKing(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupLuckyKingNoticeEvent event = eventJson.to(GroupLuckyKingNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onGroupLuckyKingNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void honor(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupHonorChangeNoticeEvent event = eventJson.to(GroupHonorChangeNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onGroupHonorChangeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

}
