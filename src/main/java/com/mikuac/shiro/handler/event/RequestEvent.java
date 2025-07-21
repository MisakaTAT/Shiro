package com.mikuac.shiro.handler.event;

import com.mikuac.shiro.common.utils.EventUtils;
import com.mikuac.shiro.common.utils.JsonObjectWrapper;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.request.FriendAddRequestEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import com.mikuac.shiro.enums.RequestEventEnum;
import com.mikuac.shiro.handler.injection.InjectionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author zero
 */
@Component
public class RequestEvent {

    private final EventUtils utils;
    private final InjectionHandler injection;

    @Autowired
    public RequestEvent(EventUtils eventUtils, InjectionHandler injectionHandler) {
        this.utils = eventUtils;
        this.injection = injectionHandler;
    }

    /**
     * 存储请求事件处理器
     */
    public final Map<String, BiConsumer<Bot, JsonObjectWrapper>> handlers = new HashMap<>();

    /**
     * 请求事件分发
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void handler(Bot bot, JsonObjectWrapper resp) {
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
     * @param resp {@link JsonObjectWrapper}
     * @param type {@link RequestEventEnum}
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored", "squid:S2201"})
    private void process(Bot bot, JsonObjectWrapper resp, RequestEventEnum type) {
        if (type == RequestEventEnum.GROUP) {
            GroupAddRequestEvent event = resp.to(GroupAddRequestEvent.class);
            injection.invokeGroupAddRequest(bot, event);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onGroupAddRequest(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }
        if (type == RequestEventEnum.FRIEND) {
            FriendAddRequestEvent event = resp.to(FriendAddRequestEvent.class);
            injection.invokeFriendAddRequest(bot, event);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onFriendAddRequest(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }
    }

    /**
     * 加好友请求
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void friend(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, RequestEventEnum.FRIEND);
    }

    /**
     * 加群请求
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void group(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, RequestEventEnum.GROUP);
    }

}
