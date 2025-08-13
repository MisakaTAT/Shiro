package com.mikuac.shiro.handler.injection;

import com.mikuac.shiro.annotation.*;
import com.mikuac.shiro.common.utils.CheckResult;
import com.mikuac.shiro.common.utils.CommonUtils;
import com.mikuac.shiro.common.utils.InternalUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.*;
import com.mikuac.shiro.dto.event.meta.HeartbeatMetaEvent;
import com.mikuac.shiro.dto.event.meta.LifecycleMetaEvent;
import com.mikuac.shiro.dto.event.notice.*;
import com.mikuac.shiro.dto.event.request.FriendAddRequestEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import com.mikuac.shiro.enums.AdminNoticeTypeEnum;
import com.mikuac.shiro.enums.CommonEnum;
import com.mikuac.shiro.enums.MetaEventEnum;
import com.mikuac.shiro.model.HandlerMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.regex.Matcher;

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
     * Invokes the handler method with dependency injection using consumerWithIndex
     *
     * @param method The handler method to invoke
     * @param params Map of available parameters for injection
     */
    private Object invokeMethod(HandlerMethod method, Map<Class<?>, Object> params) {
        Class<?>[] paramTypes = method.getMethod().getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        Arrays.stream(paramTypes).forEach(InternalUtils.consumerWithIndex((paramType, index) -> args[index] = params.get(paramType)));
        Object invokeResult = null;
        try {
            invokeResult = method.getMethod().invoke(method.getObject(), args);
        } catch (Exception e) {
            String methodName = method.getMethod().getDeclaringClass().getSimpleName()
                    + "#" + method.getMethod().getName();
            log.error("Invoke method exception on [{}]: {}", methodName, e.getMessage(), e);
        }
        return invokeResult;
    }

    private <T> void invoke(Bot bot, T event, Class<? extends Annotation> type) {
        Optional<List<HandlerMethod>> methods = Optional.ofNullable(bot.getAnnotationHandler().get(type));
        if (methods.isEmpty()) {
            return;
        }
        Map<Class<?>, Object> params = new HashMap<>();
        params.put(Bot.class, bot);
        params.put(event.getClass(), event);
        for (HandlerMethod method : methods.get()) {
            if (isBlockingResult(invokeMethod(method, params))) break;
        }
    }

    private <T> Object invoke(Bot bot, T event, HandlerMethod method, Matcher matcher) {
        Map<Class<?>, Object> params = new HashMap<>();
        // 此处逻辑修改，因为如果包含 cmd 但是校验未通过，在之前就被拦截掉了，所以到达此处若 matcher 为空则说明 cmd 参数未填写，不影响参数传递。
        if (matcher != null) {
            params.put(Matcher.class, matcher);
        }
        params.put(Bot.class, bot);
        params.put(event.getClass(), event);
        return invokeMethod(method, params);
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
     * 加好友请求事件
     *
     * @param bot   {@link Bot}
     * @param event {@link FriendAddRequestEvent}
     */
    public void invokeFriendAddRequest(Bot bot, FriendAddRequestEvent event) {
        invoke(bot, event, FriendAddRequestHandler.class);
    }

    /**
     * 加群请求事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupAddRequestEvent}
     */
    public void invokeGroupAddRequest(Bot bot, GroupAddRequestEvent event) {
        invoke(bot, event, GroupAddRequestHandler.class);
    }

    /**
     * 群禁言事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupBanNoticeEvent}
     */
    public void invokeGroupBanNotice(Bot bot, GroupBanNoticeEvent event) {
        invoke(bot, event, GroupBanNoticeHandler.class);
    }

    /**
     * 群名片变更事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupCardChangeNoticeEvent}
     */
    public void invokeGroupCardChangeNotice(Bot bot, GroupCardChangeNoticeEvent event) {
        invoke(bot, event, GroupCardChangeNoticeHandler.class);
    }

    /**
     * 群戳一戳事件
     *
     * @param bot   {@link Bot}
     * @param event {@link PokeNoticeEvent}
     */
    public void invokeGroupPokeNotice(Bot bot, PokeNoticeEvent event) {
        invoke(bot, event, GroupPokeNoticeHandler.class);
    }

    /**
     * 群文件上传事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupUploadNoticeEvent}
     */
    public void invokeGroupUploadNotice(Bot bot, GroupUploadNoticeEvent event) {
        invoke(bot, event, GroupUploadNoticeHandler.class);
    }

    /**
     * 私聊戳一戳事件
     *
     * @param bot   {@link Bot}
     * @param event {@link PokeNoticeEvent}
     */
    public void invokePrivatePokeNotice(Bot bot, PokeNoticeEvent event) {
        invoke(bot, event, PrivatePokeNoticeHandler.class);
    }

    /**
     * 群消息表情贴事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupMessageReactionNoticeEvent}
     */
    public void invokeGroupReactionNotice(Bot bot, GroupMessageReactionNoticeEvent event) {
        invoke(bot, event, GroupReactionNoticeHandler.class);
    }

    /**
     * 消息表情回应事件
     *
     * @param bot   {@link Bot}
     * @param event {@link MessageEmojiLikeNoticeEvent}
     */
    public void invokeMessageEmojiLikeNotice(Bot bot, MessageEmojiLikeNoticeEvent event) {
        invoke(bot, event, MessageEmojiLikeNoticeHandler.class);
    }

    /**
     * 监听全部消息
     *
     * @param bot   {@link Bot}
     * @param event {@link AnyMessageEvent}
     */
    public void invokeAnyMessage(Bot bot, AnyMessageEvent event) {
        Optional<List<HandlerMethod>> methods = Optional.ofNullable(bot.getAnnotationHandler().get(AnyMessageHandler.class));
        invokeMessage(bot, event, methods);
    }

    /**
     * 频道消息
     *
     * @param bot   {@link Bot}
     * @param event {@link GuildMessageEvent}
     */
    public void invokeGuildMessage(Bot bot, GuildMessageEvent event) {
        Optional<List<HandlerMethod>> methods = Optional.ofNullable(bot.getAnnotationHandler().get(GuildMessageHandler.class));
        invokeMessage(bot, event, methods);
    }

    /**
     * 群聊消息
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupMessageEvent}
     */
    public void invokeGroupMessage(Bot bot, GroupMessageEvent event) {
        Optional<List<HandlerMethod>> methods = Optional.ofNullable(bot.getAnnotationHandler().get(GroupMessageHandler.class));
        invokeMessage(bot, event, methods);
    }

    /**
     * 私聊消息
     *
     * @param bot   {@link Bot}
     * @param event {@link PrivateMessageEvent}
     */
    public void invokePrivateMessage(Bot bot, PrivateMessageEvent event) {
        Optional<List<HandlerMethod>> methods = Optional.ofNullable(bot.getAnnotationHandler().get(PrivateMessageHandler.class));
        invokeMessage(bot, event, methods);
    }

    /**
     * 处理消息的过滤器于消息的分发
     *
     * @param bot            {@link Bot}
     * @param event          {@link MessageEvent}
     * @param handlerMethods 消息处理方法
     */
    @SuppressWarnings("squid:S1121")
    public void invokeMessage(Bot bot, MessageEvent event, Optional<List<HandlerMethod>> handlerMethods) {
        if (handlerMethods.isEmpty()) {
            return;
        }
        for (HandlerMethod method : handlerMethods.get()) {
            MessageHandlerFilter filter = method.getMethod().getAnnotation(MessageHandlerFilter.class);
            CheckResult result;
            Object invokeResult = null;
            if (Objects.isNull(filter)) {
                invokeResult = invoke(bot, event, method, null);
            } else if ((result = CommonUtils.allFilterCheck(event, bot.getSelfId(), filter)).isResult()) {
                invokeResult = invoke(bot, event, method, result.getMatcher());
            }
            if (isBlockingResult(invokeResult)) break;
        }
    }

    /**
     * 管理员变动事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupAdminNoticeEvent}
     */
    public void invokeGroupAdmin(Bot bot, GroupAdminNoticeEvent event) {
        Optional<List<HandlerMethod>> methods = Optional.ofNullable(bot.getAnnotationHandler().get(GroupAdminHandler.class));
        if (methods.isEmpty()) {
            return;
        }
        methods.get().forEach(method -> {
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
            invoke(bot, event, GroupAdminHandler.class);
        });
    }

    /***
     * 心跳包
     *
     * @param bot   {@link Bot}
     * @param event {@link HeartbeatMetaEvent}
     */
    public void invokeHeartbeat(Bot bot, HeartbeatMetaEvent event) {
        Optional<List<HandlerMethod>> methods = Optional.ofNullable(bot.getAnnotationHandler().get(MetaHandler.class));
        if (methods.isEmpty()) {
            return;
        }
        Map<Class<?>, Object> params = new HashMap<>();
        params.put(Bot.class, bot);
        params.put(HeartbeatMetaEvent.class, event);
        methods.get().forEach(method -> {
            MetaHandler anno = method.getMethod().getAnnotation(MetaHandler.class);
            if (anno.type() != MetaEventEnum.HEARTBEAT) {
                return;
            }

            invokeMethod(method, params);
        });
    }

    /***
     * 生命周期
     *
     * @param bot   {@link Bot}
     * @param event {@link LifecycleMetaEvent}
     */
    public void invokeLifecycle(Bot bot, LifecycleMetaEvent event) {
        Optional<List<HandlerMethod>> methods = Optional.ofNullable(bot.getAnnotationHandler().get(MetaHandler.class));
        Map<Class<?>, Object> params = new HashMap<>();
        if (methods.isEmpty()) {
            return;
        }
        params.put(Bot.class, bot);
        params.put(LifecycleMetaEvent.class, event);
        methods.get().forEach(method -> {
            MetaHandler anno = method.getMethod().getAnnotation(MetaHandler.class);
            if (anno.type() != MetaEventEnum.LIFECYCLE) {
                return;
            }

            invokeMethod(method, params);
        });
    }

    /**
     * 判断是否为阻塞结果
     */
    private boolean isBlockingResult(Object result) {
        if (result == null) return false;
        if (result instanceof Boolean && Boolean.TRUE.equals(result)) return true;
        return result.equals(BotPlugin.MESSAGE_BLOCK);
    }

}
