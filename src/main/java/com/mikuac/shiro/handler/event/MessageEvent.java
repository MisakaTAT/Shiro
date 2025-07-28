package com.mikuac.shiro.handler.event;

import com.mikuac.shiro.common.utils.EventUtils;
import com.mikuac.shiro.common.utils.GroupMessageFilterUtils;
import com.mikuac.shiro.common.utils.JsonObjectWrapper;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.GuildMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.enums.MessageEventEnum;
import com.mikuac.shiro.handler.injection.InjectionHandler;
import com.mikuac.shiro.properties.ShiroProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author zero
 */
@Slf4j
@Component
public class MessageEvent {

    private final EventUtils utils;
    private final ShiroProperties shiroProperties;
    private final BotContainer botContainer;
    private final InjectionHandler injection;

    @Autowired
    public MessageEvent(
            EventUtils utils, ShiroProperties shiroProperties, BotContainer botContainer, InjectionHandler injection
    ) {
        this.utils = utils;
        this.shiroProperties = shiroProperties;
        this.botContainer = botContainer;
        this.injection = injection;
    }

    /**
     * 存储消息事件处理器
     */
    public final Map<String, BiConsumer<Bot, JsonObjectWrapper>> handlers = new HashMap<>();

    /**
     * 消息事件分发
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void handler(Bot bot, JsonObjectWrapper resp) {
        String type = resp.getString("message_type");
        handlers.getOrDefault(type, (b, e) -> {
        }).accept(bot, resp);
    }

    /**
     * 事件处理
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     * @param type {@link MessageEventEnum}
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored", "squid:S2201", "squid:S3776"})
    private void process(Bot bot, JsonObjectWrapper resp, MessageEventEnum type) {
        try {
            if (type == MessageEventEnum.FRIEND) {
                PrivateMessageEvent event = resp.to(PrivateMessageEvent.class);
                if (utils.setInterceptor(bot, event)) {
                    return;
                }
                resp.put("message", event.getMessage());
                utils.pushAnyMessageEvent(bot, resp, event.getArrayMsg());
                injection.invokePrivateMessage(bot, event);
                bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onPrivateMessage(bot, event) == BotPlugin.MESSAGE_BLOCK);
                utils.getInterceptor(bot.getBotMessageEventInterceptor()).afterCompletion(bot, event);
            }

            if (type == MessageEventEnum.GROUP) {
                GroupMessageEvent event = resp.to(GroupMessageEvent.class);
                if (Boolean.TRUE.equals(shiroProperties.getGroupEventFilter())) {
                    // 当开启群组消息过滤时
                    Long senderId = event.getSender().getUserId();
                    // 不满足 指定时间内无重复消息 && 发送人并非连接本机的bot实例发送 则忽略消息
                    if (!GroupMessageFilterUtils.insertMessage(event, shiroProperties.getGroupEventFilterTime())
                            || (shiroProperties.getGroupSelfBotEventFilter() && botContainer.robots.containsKey(senderId))) {
                        // 忽略此条消息
                        return;
                    }
                }
                if (utils.setInterceptor(bot, event)) {
                    return;
                }
                resp.put("message", event.getMessage());
                utils.pushAnyMessageEvent(bot, resp, event.getArrayMsg());
                injection.invokeGroupMessage(bot, event);
                bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onGroupMessage(bot, event) == BotPlugin.MESSAGE_BLOCK);
                utils.getInterceptor(bot.getBotMessageEventInterceptor()).afterCompletion(bot, event);
            }

            if (type == MessageEventEnum.GUILD) {
                GuildMessageEvent event = resp.to(GuildMessageEvent.class);
                if (utils.setInterceptor(bot, event)) {
                    return;
                }
                injection.invokeGuildMessage(bot, event);
                bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onGuildMessage(bot, event) == BotPlugin.MESSAGE_BLOCK);
                utils.getInterceptor(bot.getBotMessageEventInterceptor()).afterCompletion(bot, event);
            }
        } catch (Exception e) {
            log.error("Message event process exception: {}", e.getMessage(), e);
        }
    }

    /**
     * 私聊请求
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void friend(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, MessageEventEnum.FRIEND);
    }

    /**
     * 群消息
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void group(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, MessageEventEnum.GROUP);
    }

    /**
     * 频道消息
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void guild(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, MessageEventEnum.GUILD);
    }

}
