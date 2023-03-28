package com.mikuac.shiro.handler.injection;

import com.mikuac.shiro.annotation.*;
import com.mikuac.shiro.common.utils.CommonUtils;
import com.mikuac.shiro.common.utils.InternalUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.GuildMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.dto.event.notice.*;
import com.mikuac.shiro.enums.AdminNoticeTypeEnum;
import com.mikuac.shiro.enums.AtEnum;
import com.mikuac.shiro.enums.CommonEnum;
import com.mikuac.shiro.model.ArrayMsg;
import com.mikuac.shiro.model.HandlerMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>InjectionHandler class.</p>
 *
 * @author meme
 * @author zero
 * @version $Id: $Id
 */
@Slf4j
@Component
public class InjectionHandler {

    /**
     * @param method {@link HandlerMethod}
     * @param params params
     */
    private void invokeMethod(HandlerMethod method, Map<Class<?>, Object> params) {
        Class<?>[] parameterTypes = method.getMethod().getParameterTypes();
        Object[] objects = new Object[parameterTypes.length];
        Arrays.stream(parameterTypes).forEach(InternalUtils.consumerWithIndex((item, index) -> {
            if (params.containsKey(item)) {
                objects[index] = params.remove(item);
            } else {
                objects[index] = null;
            }
        }));
        try {
            method.getMethod().invoke(method.getObject(), objects);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            log.error("Invocation target exception: {}", t.getMessage(), t);
        } catch (Exception e) {
            log.error("Invoke exception: {}", e.getMessage(), e);
        }
    }

    /**
     * @param bot    {@link Bot}
     * @param event  {@link T}
     * @param method {@link HandlerMethod}
     * @param <T>    {@link T}
     */
    private <T> void invoke(Bot bot, T event, HandlerMethod method) {
        Map<Class<?>, Object> params = new HashMap<>();
        params.put(Bot.class, bot);
        params.put(event.getClass(), event);
        invokeMethod(method, params);
    }

    /**
     * @param bot   {@link Bot}
     * @param event {@link T}
     * @param anno  {@link Annotation}
     * @param <T>   {@link T}
     */
    private <T> void invoke(Bot bot, T event, Class<? extends Annotation> anno) {
        List<HandlerMethod> methods = bot.getAnnotationHandler().get(anno);
        if (methods == null || methods.isEmpty()) {
            return;
        }
        methods.forEach(method -> {
            Map<Class<?>, Object> params = new HashMap<>();
            params.put(Bot.class, bot);
            params.put(event.getClass(), event);
            invokeMethod(method, params);
        });
    }

    /**
     * @param bot      {@link Bot}
     * @param event    {@link T}
     * @param method   {@link HandlerMethod}
     * @param cmd      {@link  String}
     * @param msg      {@link String}
     * @param arrayMsg {@link  ArrayMsg}
     * @param at       {@link AtEnum}
     * @param selfId   {@link Long}
     * @param <T>      {@link T}
     */
    @SuppressWarnings("squid:S107")
    private <T> void invoke(Bot bot, T event, HandlerMethod method, String cmd, String msg, List<ArrayMsg> arrayMsg, AtEnum at, long selfId) {
        Map<Class<?>, Object> params;
        if (at.equals(AtEnum.OFF)) {
            params = CommonUtils.matcher(cmd, msg);
        } else {
            params = CommonUtils.matcher(cmd, CommonUtils.extractMsg(msg, arrayMsg, at, selfId));
        }
        if (params == null) {
            return;
        }
        params.put(Bot.class, bot);
        params.put(event.getClass(), event);
        invokeMethod(method, params);
    }

    /**
     * 群消息撤回事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupMsgDeleteNoticeEvent}
     */
    public void invokeGroupRecall(Bot bot, GroupMsgDeleteNoticeEvent event) {
        invoke(bot, event, GroupMsgDeleteNoticeHandler.class);
    }

    /**
     * 好友消息撤回事件
     *
     * @param bot   {@link Bot}
     * @param event {@link PrivateMsgDeleteNoticeEvent}
     */
    public void invokeFriendRecall(Bot bot, PrivateMsgDeleteNoticeEvent event) {
        invoke(bot, event, PrivateMsgDeleteNoticeHandler.class);
    }

    /**
     * 好友添加事件
     *
     * @param bot   {@link Bot}
     * @param event {@link FriendAddNoticeEvent}
     */
    public void invokeFriendAdd(Bot bot, FriendAddNoticeEvent event) {
        invoke(bot, event, FriendAddNoticeHandler.class);
    }

    /**
     * 入群事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupIncreaseNoticeEvent}
     */
    public void invokeGroupIncrease(Bot bot, GroupIncreaseNoticeEvent event) {
        invoke(bot, event, GroupIncreaseHandler.class);
    }

    /**
     * 退群事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupDecreaseNoticeEvent}
     */
    public void invokeGroupDecrease(Bot bot, GroupDecreaseNoticeEvent event) {
        invoke(bot, event, GroupDecreaseHandler.class);
    }

    /**
     * 监听全部消息
     *
     * @param bot   {@link Bot}
     * @param event {@link AnyMessageEvent}
     */
    public void invokeAnyMessage(Bot bot, AnyMessageEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> methods = handlers.get(AnyMessageHandler.class);
        if (methods != null && !methods.isEmpty()) {
            methods.forEach(method -> {
                AnyMessageHandler annotation = method.getMethod().getAnnotation(AnyMessageHandler.class);
                if (CommonEnum.GROUP.value().equals(event.getMessageType()) && CommonUtils.atCheck(event.getArrayMsg(), event.getSelfId(), annotation.at())) {
                    return;
                }
                invoke(bot, event, method, annotation.cmd(), event.getMessage(), event.getArrayMsg(), annotation.at(), event.getSelfId());
            });
        }
    }

    /**
     * 频道消息
     *
     * @param bot   {@link Bot}
     * @param event {@link GuildMessageEvent}
     */
    public void invokeGuildMessage(Bot bot, GuildMessageEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> methods = handlers.get(GuildMessageHandler.class);
        if (methods != null && !methods.isEmpty()) {
            methods.forEach(method -> {
                GuildMessageHandler annotation = method.getMethod().getAnnotation(GuildMessageHandler.class);
                if (CommonUtils.atCheck(event.getArrayMsg(), Long.parseLong(event.getSelfTinyId()), annotation.at())) {
                    return;
                }
                invoke(bot, event, method, annotation.cmd(), event.getMessage(), event.getArrayMsg(), annotation.at(), event.getSelfId());
            });
        }
    }

    /**
     * 群聊消息
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupMessageEvent}
     */
    public void invokeGroupMessage(Bot bot, GroupMessageEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> methods = handlers.get(GroupMessageHandler.class);
        if (methods != null && !methods.isEmpty()) {
            methods.forEach(method -> {
                GroupMessageHandler annotation = method.getMethod().getAnnotation(GroupMessageHandler.class);
                if (CommonUtils.atCheck(event.getArrayMsg(), event.getSelfId(), annotation.at())) {
                    return;
                }
                invoke(bot, event, method, annotation.cmd(), event.getMessage(), event.getArrayMsg(), annotation.at(), event.getSelfId());
            });
        }
    }

    /**
     * 私聊消息
     *
     * @param bot   {@link Bot}
     * @param event {@link PrivateMessageEvent}
     */
    public void invokePrivateMessage(Bot bot, PrivateMessageEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> methods = handlers.get(PrivateMessageHandler.class);
        if (methods != null && !methods.isEmpty()) {
            methods.forEach(method -> {
                PrivateMessageHandler annotation = method.getMethod().getAnnotation(PrivateMessageHandler.class);
                invoke(bot, event, method, annotation.cmd(), event.getMessage(), event.getArrayMsg(), AtEnum.OFF, event.getSelfId());
            });
        }
    }

    /**
     * 管理员变动事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupAdminNoticeEvent}
     */
    public void invokeGroupAdmin(Bot bot, GroupAdminNoticeEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> methods = handlers.get(GroupAdminHandler.class);
        if (methods != null && !methods.isEmpty()) {
            methods.forEach(method -> {
                AdminNoticeTypeEnum type = method.getMethod().getAnnotation(GroupAdminHandler.class).type();
                if (type == AdminNoticeTypeEnum.OFF) {
                    return;
                }
                if (type == AdminNoticeTypeEnum.UNSET && !CommonEnum.UNSET.value().equals(event.getSubType())) {
                    return;
                }
                if (type == AdminNoticeTypeEnum.SET && !CommonEnum.SET.value().equals(event.getSubType())) {
                    return;
                }
                invoke(bot, event, method);
            });
        }

    }

}
