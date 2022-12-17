package com.mikuac.shiro.handler;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.handler.event.*;
import com.mikuac.shiro.handler.injection.InjectionHandler;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 事件处理器
 *
 * @author zero
 * @version $Id: $Id
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class EventHandler implements ApplicationRunner {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private MetaEvent meta;

    @Resource
    private NoticeEvent notice;

    @Resource
    private NotifyEvent notify;

    @Resource
    private MessageEvent message;

    @Resource
    private RequestEvent request;

    @Resource
    private InjectionHandler injection;

    /**
     * 存储事件处理器
     */
    private final Map<String, BiConsumer<Bot, JSONObject>> handlers = new HashMap<>();

    @Override
    public void run(ApplicationArguments args) {
        // Register Event Handler
        handlers.put("meta_event", meta::handler);
        handlers.put("message", message::handler);
        handlers.put("notice", notice::handler);
        handlers.put("request", request::handler);

        // Register Message Handler
        message.handlers.put("private", message::friend);
        message.handlers.put("group", message::group);
        message.handlers.put("guild", message::guild);

        // Register Notice Handler
        notice.handlers.put("notify", notice::notify);
        notice.handlers.put("group_upload", notice::groupUpload);
        notice.handlers.put("group_admin", notice::groupAdmin);
        notice.handlers.put("group_decrease", notice::groupDecrease);
        notice.handlers.put("group_increase", notice::groupIncrease);
        notice.handlers.put("group_ban", notice::groupBan);
        notice.handlers.put("friend_add", notice::friendAdd);
        notice.handlers.put("group_recall", notice::groupMsgDelete);
        notice.handlers.put("friend_recall", notice::privateMsgDelete);
        notice.handlers.put("group_card", notice::groupCardChange);
        notice.handlers.put("offline_file", notice::offlineFile);
        notice.handlers.put("channel_created", notice::channelCreated);
        notice.handlers.put("channel_destroyed", notice::channelDestroyed);
        notice.handlers.put("channel_updated", notice::channelUpdated);
        notice.handlers.put("message_reactions_updated", notice::messageReactionsUpdated);

        // Register Notify Handler
        notify.handlers.put("poke", notify::poke);
        notify.handlers.put("lucky_king", notify::luckyKing);
        notify.handlers.put("honor", notify::honor);

        // Register Request Handler
        request.handlers.put("friend", request::friend);
        request.handlers.put("group", request::group);
    }

    /**
     * 事件分发
     *
     * @param bot       {@link Bot}
     * @param eventJson {@link JSONObject}
     */
    public void handler(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        String postType = eventJson.getString("post_type");
        handlers.getOrDefault(postType, (b, e) -> {
        }).accept(bot, eventJson);
    }

}
