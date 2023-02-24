package com.mikuac.shiro.handler.injection;

import com.mikuac.shiro.annotation.*;
import com.mikuac.shiro.bo.ArrayMsg;
import com.mikuac.shiro.bo.HandlerMethod;
import com.mikuac.shiro.common.utils.InternalUtils;
import com.mikuac.shiro.common.utils.RegexUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.GuildMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.dto.event.notice.*;
import com.mikuac.shiro.enums.AdminNoticeTypeEnum;
import com.mikuac.shiro.enums.AtEnum;
import com.mikuac.shiro.enums.CommonEnum;
import com.mikuac.shiro.enums.MsgTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * 群消息撤回事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupMsgDeleteNoticeEvent}
     */
    public void invokeGroupRecall(Bot bot, GroupMsgDeleteNoticeEvent event) {
        setParams(bot.getAnnotationHandler().get(GroupMsgDeleteNoticeHandler.class), bot, event);
    }

    /**
     * 好友消息撤回事件
     *
     * @param bot   {@link Bot}
     * @param event {@link PrivateMsgDeleteNoticeEvent}
     */
    public void invokeFriendRecall(Bot bot, PrivateMsgDeleteNoticeEvent event) {
        setParams(bot.getAnnotationHandler().get(PrivateMsgDeleteNoticeHandler.class), bot, event);
    }

    /**
     * 好友添加事件
     *
     * @param bot   {@link Bot}
     * @param event {@link FriendAddNoticeEvent}
     */
    public void invokeFriendAdd(Bot bot, FriendAddNoticeEvent event) {
        setParams(bot.getAnnotationHandler().get(FriendAddNoticeHandler.class), bot, event);
    }

    /**
     * 入群事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupIncreaseNoticeEvent}
     */
    public void invokeGroupIncrease(Bot bot, GroupIncreaseNoticeEvent event) {
        setParams(bot.getAnnotationHandler().get(GroupIncreaseHandler.class), bot, event);
    }

    /**
     * 退群事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupDecreaseNoticeEvent}
     */
    public void invokeGroupDecrease(Bot bot, GroupDecreaseNoticeEvent event) {
        setParams(bot.getAnnotationHandler().get(GroupDecreaseHandler.class), bot, event);
    }

    /**
     * 监听全部消息
     *
     * @param bot   {@link Bot}
     * @param event {@link AnyMessageEvent}
     */
    public void invokeAnyMessage(Bot bot, AnyMessageEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> handlerMethods = handlers.get(AnyMessageHandler.class);
        if (handlerMethods != null && !handlerMethods.isEmpty()) {
            handlerMethods.forEach(handlerMethod -> {
                AnyMessageHandler annotation = handlerMethod.getMethod().getAnnotation(AnyMessageHandler.class);
                if (CommonEnum.GROUP.value().equals(event.getMessageType()) && atCheck(event.getArrayMsg(), event.getSelfId(), annotation.at())) {
                    return;
                }
                Map<Class<?>, Object> params = matcher(annotation.cmd(), extractMsg(event.getMessage(), event.getArrayMsg(), annotation.at(), event.getSelfId()));
                if (params == null) {
                    return;
                }
                params.put(Bot.class, bot);
                params.put(AnyMessageEvent.class, event);
                invokeMethod(handlerMethod, params);
            });
        }
    }

    /**
     * @param arrayMsg 消息链
     * @param selfId   机器人QQ
     * @param at       {@link AtEnum}
     * @return 是否通过检查
     */
    private boolean atCheck(List<ArrayMsg> arrayMsg, long selfId, AtEnum at) {
        ArrayMsg item = parseAt(arrayMsg, selfId);
        switch (at) {
            case NEED -> {
                if (item == null) {
                    return true;
                }
                long target = Long.parseLong(item.getData().get("qq"));
                if (target == 0L || target != selfId) {
                    return true;
                }
            }
            case NOT_NEED -> {
                if (item == null) {
                    return false;
                }
                long target = Long.parseLong(item.getData().get("qq"));
                return target == selfId;
            }
            default -> {
                return false;
            }
        }
        return false;
    }

    /**
     * 频道消息
     *
     * @param bot   {@link Bot}
     * @param event {@link GuildMessageEvent}
     */
    public void invokeGuildMessage(Bot bot, GuildMessageEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> handlerMethods = handlers.get(GuildMessageHandler.class);
        if (handlerMethods != null && !handlerMethods.isEmpty()) {
            handlerMethods.forEach(handlerMethod -> {
                GuildMessageHandler annotation = handlerMethod.getMethod().getAnnotation(GuildMessageHandler.class);
                if (atCheck(event.getArrayMsg(), Long.parseLong(event.getSelfTinyId()), annotation.at())) {
                    return;
                }
                Map<Class<?>, Object> params = matcher(annotation.cmd(), extractMsg(event.getMessage(), event.getArrayMsg(), annotation.at(), event.getSelfId()));
                if (params == null) {
                    return;
                }
                params.put(Bot.class, bot);
                params.put(GuildMessageEvent.class, event);
                invokeMethod(handlerMethod, params);
            });
        }
    }

    /**
     * 提取去除@后的消息内容
     *
     * @param msg      原始消息
     * @param arrayMsg {@link List} of {@link ArrayMsg}
     * @param atEnum   {@link AtEnum}
     * @return 处理后的消息
     */
    private String extractMsg(String msg, List<ArrayMsg> arrayMsg, AtEnum atEnum, long selfId) {
        if (atEnum != AtEnum.NEED) {
            return msg;
        }
        ArrayMsg item = parseAt(arrayMsg, selfId);
        if (item != null) {
            String code = ShiroUtils.arrayMsgToCode(arrayMsg.get(arrayMsg.indexOf(item)));
            return msg.replace(code, "").trim();
        }
        return msg;
    }

    /**
     * @param arrayMsg {@link List} of {@link ArrayMsg}
     * @param selfId   机器人账号
     * @return {@link ArrayMsg}
     */
    private ArrayMsg parseAt(List<ArrayMsg> arrayMsg, long selfId) {
        if (arrayMsg.isEmpty()) {
            return null;
        }
        int index = 0;
        ArrayMsg item = arrayMsg.get(index);
        String rawTarget = item.getData().getOrDefault("qq", "0");
        long target = Long.parseLong(CommonEnum.AT_ALL.value().equals(rawTarget) ? "0" : rawTarget);
        index = arrayMsg.size() - 1;
        if ((target == 0L || target != selfId) && index >= 0) {
            item = arrayMsg.get(index);
            // @ 右侧可能会有空格
            index = arrayMsg.size() - 2;
            if (MsgTypeEnum.text == item.getType() && index >= 0) {
                item = arrayMsg.get(index);
            }
            rawTarget = item.getData().getOrDefault("qq", "0");
            target = Long.parseLong(CommonEnum.AT_ALL.value().equals(rawTarget) ? "0" : rawTarget);
            if (target == 0L || target != selfId) {
                return null;
            }
        }
        return item;
    }

    /**
     * 群聊消息
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupMessageEvent}
     */
    public void invokeGroupMessage(Bot bot, GroupMessageEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> handlerMethods = handlers.get(GroupMessageHandler.class);
        if (handlerMethods != null && !handlerMethods.isEmpty()) {
            handlerMethods.forEach(handlerMethod -> {
                GroupMessageHandler annotation = handlerMethod.getMethod().getAnnotation(GroupMessageHandler.class);
                if (atCheck(event.getArrayMsg(), event.getSelfId(), annotation.at())) {
                    return;
                }
                Map<Class<?>, Object> params = matcher(annotation.cmd(), extractMsg(event.getMessage(), event.getArrayMsg(), annotation.at(), event.getSelfId()));
                if (params == null) {
                    return;
                }
                params.put(Bot.class, bot);
                params.put(GroupMessageEvent.class, event);
                invokeMethod(handlerMethod, params);
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
        List<HandlerMethod> handlerMethods = handlers.get(PrivateMessageHandler.class);
        if (handlerMethods != null && !handlerMethods.isEmpty()) {
            handlerMethods.forEach(handlerMethod -> {
                PrivateMessageHandler annotation = handlerMethod.getMethod().getAnnotation(PrivateMessageHandler.class);
                Map<Class<?>, Object> params = matcher(annotation.cmd(), event.getMessage());
                if (params == null) {
                    return;
                }
                params.put(PrivateMessageEvent.PrivateSender.class, event.getPrivateSender());
                params.put(Bot.class, bot);
                params.put(PrivateMessageEvent.class, event);
                invokeMethod(handlerMethod, params);
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
        List<HandlerMethod> handlerMethods = handlers.get(GroupAdminHandler.class);
        if (handlerMethods != null && !handlerMethods.isEmpty()) {
            handlerMethods.forEach(handlerMethod -> {
                AdminNoticeTypeEnum type = handlerMethod.getMethod().getAnnotation(GroupAdminHandler.class).type();
                if (type == AdminNoticeTypeEnum.OFF) {
                    return;
                }
                if (type == AdminNoticeTypeEnum.UNSET && !CommonEnum.UNSET.value().equals(event.getSubType())) {
                    return;
                }
                if (type == AdminNoticeTypeEnum.SET && !CommonEnum.SET.value().equals(event.getSubType())) {
                    return;
                }
                Map<Class<?>, Object> params = new HashMap<>();
                params.put(Bot.class, bot);
                params.put(GroupAdminNoticeEvent.class, event);
                invokeMethod(handlerMethod, params);
            });
        }

    }

    /**
     * @param handlerMethod {@link HandlerMethod}
     * @param params        params
     */
    private void invokeMethod(HandlerMethod handlerMethod, Map<Class<?>, Object> params) {
        Class<?>[] parameterTypes = handlerMethod.getMethod().getParameterTypes();
        Object[] objects = new Object[parameterTypes.length];
        Arrays.stream(parameterTypes).forEach(InternalUtils.consumerWithIndex((item, index) -> {
            if (params.containsKey(item)) {
                objects[index] = params.remove(item);
            } else {
                objects[index] = null;
            }
        }));
        try {
            handlerMethod.getMethod().invoke(handlerMethod.getObject(), objects);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            log.error("Invocation target exception: {}", t.getMessage(), t);
        } catch (Exception e) {
            log.error("Invoke exception: {}", e.getMessage(), e);
        }
    }

    /**
     * 参数设置
     *
     * @param handlerMethods {@link HandlerMethod}
     * @param bot            {@link Bot}
     * @param event          {@link Object}
     */
    private void setParams(List<HandlerMethod> handlerMethods, Bot bot, Object event) {
        if (handlerMethods != null && !handlerMethods.isEmpty()) {
            handlerMethods.forEach(handlerMethod -> {
                Map<Class<?>, Object> params = new HashMap<>();
                params.put(Bot.class, bot);
                params.put(event.getClass(), event);
                invokeMethod(handlerMethod, params);
            });
        }
    }

    /**
     * 返回匹配的消息 Matcher 类
     *
     * @param cmd 正则表达式
     * @param msg 消息内容
     * @return params
     */
    @SuppressWarnings("squid:S1168")
    private Map<Class<?>, Object> matcher(String cmd, String msg) {
        Map<Class<?>, Object> params = new HashMap<>();
        if (!CommonEnum.DEFAULT_CMD.value().equals(cmd)) {
            Matcher matcher = RegexUtils.regexMatcher(cmd, msg);
            if (matcher == null) {
                return null;
            }
            params.put(Matcher.class, matcher);
        }
        return params;
    }

}
