package com.mikuac.shiro.handler.event;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.notice.GroupHonorChangeNoticeEvent;
import com.mikuac.shiro.dto.event.notice.GroupLuckyKingNoticeEvent;
import com.mikuac.shiro.dto.event.notice.PokeNoticeEvent;
import com.mikuac.shiro.handler.Handler;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zero
 */
@Component
public class NotifyHandler {

    @Resource
    private Handler handler;

    public void poke(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        PokeNoticeEvent event = eventJson.to(PokeNoticeEvent.class);
        // 如果群号不为空则当作群内戳一戳处理
        if (event.getGroupId() > 0L) {
            for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                if (handler.getPlugin(pluginClass).onGroupPokeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                    break;
                }
            }
            return;
        }
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onPrivatePokeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void luckyKing(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupLuckyKingNoticeEvent event = eventJson.to(GroupLuckyKingNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onGroupLuckyKingNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void honor(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupHonorChangeNoticeEvent event = eventJson.to(GroupHonorChangeNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onGroupHonorChangeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

}
