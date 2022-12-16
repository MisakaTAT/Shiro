package com.mikuac.shiro.handler;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.bo.ArrayMsg;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotMessageEventInterceptor;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.core.DefaultBotMessageEventInterceptor;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
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
import java.util.List;
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
public class Handler implements ApplicationRunner {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private EventHandler event;

    @Resource
    private NoticeHandler notice;

    @Resource
    private MessageHandler message;

    @Resource
    private NotifyHandler notify;

    @Resource
    private RequestHandler request;

    @Resource
    private InjectionHandler injection;

    private final BotPlugin defaultPlugin = new BotPlugin();

    public final Map<String, BiConsumer<Bot, JSONObject>> eventHandlers = new HashMap<>();

    public final Map<String, BiConsumer<Bot, JSONObject>> noticeHandlers = new HashMap<>();

    public final Map<String, BiConsumer<Bot, JSONObject>> notifyHandlers = new HashMap<>();

    public final Map<String, BiConsumer<Bot, JSONObject>> requestHandlers = new HashMap<>();

    public final Map<String, BiConsumer<Bot, JSONObject>> messageHandlers = new HashMap<>();

    @Override
    public void run(ApplicationArguments args) {
        // Register Main Handler
        eventHandlers.put("meta_event", event::meta);
        eventHandlers.put("message", event::message);
        eventHandlers.put("notice", event::notice);
        eventHandlers.put("request", event::request);

        // Register Message Handler
        messageHandlers.put("private", message::friend);
        messageHandlers.put("group", message::group);
        messageHandlers.put("guild", message::guild);

        // Register Notice Handler
        noticeHandlers.put("notify", notice::notify);
        noticeHandlers.put("group_upload", notice::groupUpload);
        noticeHandlers.put("group_admin", notice::groupAdmin);
        noticeHandlers.put("group_decrease", notice::groupDecrease);
        noticeHandlers.put("group_increase", notice::groupIncrease);
        noticeHandlers.put("group_ban", notice::groupBan);
        noticeHandlers.put("friend_add", notice::friendAdd);
        noticeHandlers.put("group_recall", notice::groupMsgDelete);
        noticeHandlers.put("friend_recall", notice::privateMsgDelete);
        noticeHandlers.put("group_card", notice::groupCardChange);
        noticeHandlers.put("offline_file", notice::offlineFile);
        noticeHandlers.put("channel_created", notice::channelCreated);
        noticeHandlers.put("channel_destroyed", notice::channelDestroyed);
        noticeHandlers.put("channel_updated", notice::channelUpdated);
        noticeHandlers.put("message_reactions_updated", notice::messageReactionsUpdated);

        // Register Notify Handler
        notifyHandlers.put("poke", notify::poke);
        notifyHandlers.put("lucky_king", notify::luckyKing);
        notifyHandlers.put("honor", notify::honor);

        // Register Request Handler
        requestHandlers.put("friend", request::friend);
        requestHandlers.put("group", request::group);
    }

    /**
     * 事件分发
     *
     * @param bot       {@link Bot}
     * @param eventJson {@link JSONObject}
     */
    public void handler(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        String postType = eventJson.getString("post_type");
        eventHandlers.getOrDefault(postType, (b, e) -> {
        }).accept(bot, eventJson);
    }

    /**
     * 获取插件
     *
     * @param pluginClass {@link Class}
     * @return {@link BotPlugin}
     */
    public BotPlugin getPlugin(Class<? extends BotPlugin> pluginClass) {
        try {
            return applicationContext.getBean(pluginClass);
        } catch (Exception e) {
            log.warn("Plugin {} skip, Please check @Component annotation", pluginClass.getSimpleName());
            return defaultPlugin;
        }
    }

    /**
     * 获取拦截器
     *
     * @param interceptorClass {@link Class}
     * @return {@link BotMessageEventInterceptor}
     */
    public BotMessageEventInterceptor getInterceptor(Class<? extends BotMessageEventInterceptor> interceptorClass) {
        try {
            return applicationContext.getBean(interceptorClass);
        } catch (Exception e) {
            log.warn("Interceptor {} skip, Please check @Component annotation", interceptorClass.getSimpleName());
            return applicationContext.getBean(DefaultBotMessageEventInterceptor.class);
        }
    }

    /**
     * 设置拦截器
     *
     * @param bot   {@link Bot}
     * @param event {@link MessageEvent}
     * @return boolean
     */
    public boolean setInterceptor(@NotNull Bot bot, @NotNull MessageEvent event) {
        try {
            return !getInterceptor(bot.getBotMessageEventInterceptor()).preHandle(bot, event);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 推送消息
     *
     * @param bot       {@link Bot}
     * @param eventJson {@link JSONObject}
     * @param arrayMsg  {@link ArrayMsg}
     */
    private void pushAnyMessageEvent(@NotNull Bot bot, @NotNull JSONObject eventJson, List<ArrayMsg> arrayMsg) {
        AnyMessageEvent event = eventJson.to(AnyMessageEvent.class);
        event.setArrayMsg(arrayMsg);
        injection.invokeAnyMessage(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (getPlugin(pluginClass).onAnyMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    /**
     * 推送消息并返回消息链
     *
     * @param bot       {@link Bot}
     * @param eventJson {@link JSONObject}
     * @param event     {@link MessageEvent}
     * @return {@link ArrayMsg}
     */
    public List<ArrayMsg> setAnyMessageEvent(@NotNull Bot bot, @NotNull JSONObject eventJson, @NotNull MessageEvent event) {
        try {
            List<ArrayMsg> arrayMsg = ShiroUtils.stringToMsgChain(event.getMessage());
            pushAnyMessageEvent(bot, eventJson, arrayMsg);
            return arrayMsg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
