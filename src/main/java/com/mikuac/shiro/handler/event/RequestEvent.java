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
@SuppressWarnings("ResultOfMethodCallIgnored")
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

    /**
     * 请求事件类型枚举
     */
    private enum RequestEventType {
        /**
         * 加群请求
         */
        GROUP,
        /**
         * 加好友请求
         */
        FRIEND
    }

    /**
     * 事件处理
     *
     * @param bot       {@link Bot}
     * @param eventJson {@link  JSONObject}
     * @param type      {@link RequestEventType}
     */
    private void process(Bot bot, JSONObject eventJson, RequestEventType type) {
        bot.getPluginList().stream().anyMatch(o -> {
            if (type == RequestEventType.GROUP) {
                return utils.getPlugin(o).onGroupAddRequest(bot, eventJson.to(GroupAddRequestEvent.class)) == BotPlugin.MESSAGE_BLOCK;
            }
            if (type == RequestEventType.FRIEND) {
                return utils.getPlugin(o).onFriendAddRequest(bot, eventJson.to(FriendAddRequestEvent.class)) == BotPlugin.MESSAGE_BLOCK;
            }
            return false;
        });
    }

    /**
     * 加好友请求
     *
     * @param bot       {@link Bot}
     * @param eventJson {@link  JSONObject}
     */
    public void friend(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        process(bot, eventJson, RequestEventType.FRIEND);
    }

    /**
     * 加群请求
     *
     * @param bot       {@link Bot}
     * @param eventJson {@link  JSONObject}
     */
    public void group(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        process(bot, eventJson, RequestEventType.GROUP);
    }

}
