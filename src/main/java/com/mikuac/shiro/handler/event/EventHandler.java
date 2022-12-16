package com.mikuac.shiro.handler.event;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.handler.Handler;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zero
 */
@Component
public class EventHandler {

    @Resource
    private Handler handler;

    public void meta(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        // Ignored this handler
    }

    public void notice(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        String type = eventJson.getString("notice_type");
        handler.noticeHandlers.getOrDefault(
                type,
                (b, e) -> {
                }
        ).accept(bot, eventJson);
    }

    public void message(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        String type = eventJson.getString("message_type");
        handler.messageHandlers.getOrDefault(
                type,
                (b, e) -> {
                }
        ).accept(bot, eventJson);
    }

    public void notify(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        String type = eventJson.getString("sub_type");
        handler.notifyHandlers.getOrDefault(
                type,
                (b, e) -> {
                }
        ).accept(bot, eventJson);
    }

    public void request(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        String type = eventJson.getString("request_type");
        handler.requestHandlers.getOrDefault(
                type,
                (b, e) -> {
                }
        ).accept(bot, eventJson);
    }

}
