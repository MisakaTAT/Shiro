package com.mikuac.shiro.handler.event;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.common.utils.EventUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.notice.GroupHonorChangeNoticeEvent;
import com.mikuac.shiro.dto.event.notice.GroupLuckyKingNoticeEvent;
import com.mikuac.shiro.dto.event.notice.PokeNoticeEvent;
import com.mikuac.shiro.enums.NotifyEventEnum;
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
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void handler(@NotNull Bot bot, @NotNull JSONObject resp) {
        String type = resp.getString("sub_type");
        handlers.getOrDefault(type, (b, e) -> {
        }).accept(bot, resp);
    }

    /**
     * 事件处理
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     * @param type {@link NotifyEventEnum}
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void process(@NotNull Bot bot, JSONObject resp, NotifyEventEnum type) {
        bot.getPluginList().stream().anyMatch(o -> {
            int status = BotPlugin.MESSAGE_IGNORE;

            if (type == NotifyEventEnum.POKE) {
                PokeNoticeEvent event = resp.to(PokeNoticeEvent.class);
                // 如果群号不为空则作为群内戳一戳处理
                if (event.getGroupId() > 0L) {
                    status = utils.getPlugin(o).onGroupPokeNotice(bot, event);
                } else {
                    status = utils.getPlugin(o).onPrivatePokeNotice(bot, event);
                }
            }

            if (type == NotifyEventEnum.HONOR) {
                status = utils.getPlugin(o).onGroupHonorChangeNotice(bot, resp.to(GroupHonorChangeNoticeEvent.class));
            }

            if (type == NotifyEventEnum.LUCKY_KING) {
                status = utils.getPlugin(o).onGroupLuckyKingNotice(bot, resp.to(GroupLuckyKingNoticeEvent.class));
            }

            return status == BotPlugin.MESSAGE_BLOCK;
        });
    }

    /**
     * 戳一戳事件
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void poke(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NotifyEventEnum.POKE);
    }

    /**
     * 抢红包运气王事件
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void luckyKing(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NotifyEventEnum.LUCKY_KING);
    }

    /**
     * 群荣誉变更事件
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void honor(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NotifyEventEnum.HONOR);
    }

}
