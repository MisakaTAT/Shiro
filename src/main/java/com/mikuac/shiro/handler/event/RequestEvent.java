package com.mikuac.shiro.handler.event;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.common.utils.EventUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.request.FriendAddRequestEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import com.mikuac.shiro.enums.RequestEventEnum;
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
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void handler(@NotNull Bot bot, @NotNull JSONObject resp) {
        String type = resp.getString("request_type");
        handlers.getOrDefault(
                type,
                (b, e) -> {
                }
        ).accept(bot, resp);
    }

    /**
     * 事件处理
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     * @param type {@link RequestEventEnum}
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void process(@NotNull Bot bot, JSONObject resp, RequestEventEnum type) {
        bot.getPluginList().stream().anyMatch(o -> {
            int status = BotPlugin.MESSAGE_IGNORE;

            if (type == RequestEventEnum.GROUP) {
                status = utils.getPlugin(o).onGroupAddRequest(bot, resp.to(GroupAddRequestEvent.class));
            }
            if (type == RequestEventEnum.FRIEND) {
                status = utils.getPlugin(o).onFriendAddRequest(bot, resp.to(FriendAddRequestEvent.class));
            }

            return status == BotPlugin.MESSAGE_BLOCK;
        });
    }

    /**
     * 加好友请求
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void friend(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, RequestEventEnum.FRIEND);
    }

    /**
     * 加群请求
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void group(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, RequestEventEnum.GROUP);
    }

}
