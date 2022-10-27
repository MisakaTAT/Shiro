package com.mikuac.shiro.handler.injection;

import com.mikuac.shiro.annotation.*;
import com.mikuac.shiro.bean.HandlerMethod;
import com.mikuac.shiro.bean.MsgChainBean;
import com.mikuac.shiro.common.utils.InternalUtils;
import com.mikuac.shiro.common.utils.RegexUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.GuildMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.dto.event.notice.*;
import com.mikuac.shiro.enums.AtEnum;
import com.mikuac.shiro.enums.CommonEnum;
import lombok.val;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author meme
 * @author zero
 */
@Component
public class InjectionHandler {

    /**
     * 群消息撤回事件
     *
     * @param bot   {@link com.mikuac.shiro.core.Bot}
     * @param event {@link com.mikuac.shiro.dto.event.notice.GroupMsgDeleteNoticeEvent}
     */
    public void invokeGroupRecall(@NotNull Bot bot, @NotNull GroupMsgDeleteNoticeEvent event) {
        setParams(bot.getAnnotationHandler().get(GroupMsgDeleteNoticeHandler.class), bot, event);
    }

    /**
     * 好友消息撤回事件
     *
     * @param bot   {@link com.mikuac.shiro.core.Bot}
     * @param event {@link com.mikuac.shiro.dto.event.notice.PrivateMsgDeleteNoticeEvent}
     */
    public void invokeFriendRecall(@NotNull Bot bot, @NotNull PrivateMsgDeleteNoticeEvent event) {
        setParams(bot.getAnnotationHandler().get(PrivateMsgDeleteNoticeHandler.class), bot, event);
    }

    /**
     * 好友添加事件
     *
     * @param bot   {@link com.mikuac.shiro.core.Bot}
     * @param event {@link com.mikuac.shiro.dto.event.notice.FriendAddNoticeEvent}
     */
    public void invokeFriendAdd(@NotNull Bot bot, @NotNull FriendAddNoticeEvent event) {
        setParams(bot.getAnnotationHandler().get(FriendAddNoticeHandler.class), bot, event);
    }

    /**
     * 入群事件
     *
     * @param bot   {@link com.mikuac.shiro.core.Bot}
     * @param event {@link com.mikuac.shiro.dto.event.notice.GroupIncreaseNoticeEvent}
     */
    public void invokeGroupIncrease(@NotNull Bot bot, @NotNull GroupIncreaseNoticeEvent event) {
        setParams(bot.getAnnotationHandler().get(GroupIncreaseHandler.class), bot, event);
    }

    /**
     * 退群事件
     *
     * @param bot   {@link com.mikuac.shiro.core.Bot}
     * @param event {@link com.mikuac.shiro.dto.event.notice.GroupDecreaseNoticeEvent}
     */
    public void invokeGroupDecrease(@NotNull Bot bot, @NotNull GroupDecreaseNoticeEvent event) {
        setParams(bot.getAnnotationHandler().get(GroupDecreaseHandler.class), bot, event);
    }

    /**
     * 监听全部消息
     *
     * @param bot   {@link com.mikuac.shiro.core.Bot}
     * @param event {@link com.mikuac.shiro.dto.event.message.AnyMessageEvent}
     */
    public void invokeAnyMessage(@NotNull Bot bot, @NotNull AnyMessageEvent event) {
        val handlers = bot.getAnnotationHandler();
        val handlerMethods = handlers.get(MessageHandler.class);
        if (handlerMethods != null && !handlerMethods.isEmpty()) {
            handlerMethods.forEach(handlerMethod -> {
                val annotation = handlerMethod.getMethod().getAnnotation(MessageHandler.class);
                if (CommonEnum.GROUP.value().equals(event.getMessageType())) {
                    if (checkAt(event.getArrayMsg(), event.getSelfId(), annotation.at())) {
                        return;
                    }
                }
                val msg = extractMsg(event.getMessage(), event.getArrayMsg(), annotation.at());
                val params = matcher(annotation.cmd(), msg);
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
     * at 检查
     *
     * @param arrayMsg 消息链
     * @param selfId   机器人QQ
     * @param atEnum   at枚举
     * @return boolean
     */
    private boolean checkAt(List<MsgChainBean> arrayMsg, long selfId, AtEnum atEnum) {
        val at = "at";
        val all = "all";

        if (atEnum == AtEnum.OFF) {
            return false;
        }

        if (arrayMsg.isEmpty()) {
            return true;
        }

        if (atEnum == AtEnum.NEED) {
            val atObj = arrayMsg.get(0);
            val atUserIdStr = atObj.getData().get("qq");
            if (!at.equals(atObj.getType())) {
                return true;
            }
            if (atUserIdStr == null || atUserIdStr.isEmpty()) {
                return false;
            }
            if (all.equals(atUserIdStr)) {
                return true;
            }
            val atUserId = Long.parseLong(atUserIdStr);
            return selfId != atUserId;
        }

        if (atEnum == AtEnum.NOT_NEED) {
            val atObj = arrayMsg.get(0);
            val atUserIdStr = atObj.getData().get("qq");
            if (atUserIdStr == null || atUserIdStr.isEmpty()) {
                return false;
            }
            if (all.equals(atUserIdStr)) {
                return false;
            }
            val atUserId = Long.parseLong(atUserIdStr);
            return selfId == atUserId;
        }

        return true;
    }

    /**
     * 频道消息
     *
     * @param bot   {@link com.mikuac.shiro.core.Bot}
     * @param event {@link com.mikuac.shiro.dto.event.message.GuildMessageEvent}
     */
    public void invokeGuildMessage(@NotNull Bot bot, @NotNull GuildMessageEvent event) {
        val handlers = bot.getAnnotationHandler();
        val handlerMethods = handlers.get(GuildMessageHandler.class);
        if (handlerMethods != null && !handlerMethods.isEmpty()) {
            handlerMethods.forEach(handlerMethod -> {
                val annotation = handlerMethod.getMethod().getAnnotation(GuildMessageHandler.class);
                if (checkAt(event.getArrayMsg(), Long.parseLong(event.getSelfTinyId()), annotation.at())) {
                    return;
                }
                val msg = extractMsg(event.getMessage(), event.getArrayMsg(), annotation.at());
                val params = matcher(annotation.cmd(), msg);
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
     * @param message  原始消息
     * @param arrayMsg 数组消息
     * @param atEnum   @枚举
     * @return 处理后的消息
     */
    private String extractMsg(String message, List<MsgChainBean> arrayMsg, AtEnum atEnum) {
        var msg = message;
        if (atEnum == AtEnum.NEED) {
            val atCode = ShiroUtils.jsonToCode(arrayMsg.get(0));
            msg = msg.replace(atCode, "").replace(" ", "");
        }
        return msg;
    }

    /**
     * 群聊消息
     *
     * @param bot   {@link com.mikuac.shiro.core.Bot}
     * @param event {@link com.mikuac.shiro.dto.event.message.GroupMessageEvent}
     */
    public void invokeGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        val handlers = bot.getAnnotationHandler();
        val handlerMethods = handlers.get(GroupMessageHandler.class);
        if (handlerMethods != null && !handlerMethods.isEmpty()) {
            handlerMethods.forEach(handlerMethod -> {
                val annotation = handlerMethod.getMethod().getAnnotation(GroupMessageHandler.class);
                if (checkAt(event.getArrayMsg(), event.getSelfId(), annotation.at())) {
                    return;
                }
                val msg = extractMsg(event.getMessage(), event.getArrayMsg(), annotation.at());
                val params = matcher(annotation.cmd(), msg);
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
     * @param bot   {@link com.mikuac.shiro.core.Bot}
     * @param event {@link com.mikuac.shiro.dto.event.message.PrivateMessageEvent}
     */
    public void invokePrivateMessage(@NotNull Bot bot, @NotNull PrivateMessageEvent event) {
        val handlers = bot.getAnnotationHandler();
        val handlerMethods = handlers.get(PrivateMessageHandler.class);
        if (handlerMethods != null && !handlerMethods.isEmpty()) {
            handlerMethods.forEach(handlerMethod -> {
                val annotation = handlerMethod.getMethod().getAnnotation(PrivateMessageHandler.class);
                val params = matcher(annotation.cmd(), event.getMessage());
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
     * @param bot   {@link com.mikuac.shiro.core.Bot}
     * @param event {@link com.mikuac.shiro.dto.event.notice.GroupAdminNoticeEvent}
     */
    public void invokeGroupAdmin(@NotNull Bot bot, @NotNull GroupAdminNoticeEvent event) {
        val handlers = bot.getAnnotationHandler();
        val handlerMethods = handlers.get(GroupAdminHandler.class);
        if (handlerMethods != null && !handlerMethods.isEmpty()) {
            handlerMethods.forEach(handlerMethod -> {
                switch (handlerMethod.getMethod().getAnnotation(GroupAdminHandler.class).type()) {
                    case OFF:
                        return;
                    case ALL:
                        break;
                    case UNSET:
                        if (!CommonEnum.UNSET.value().equals(event.getSubType())) {
                            return;
                        }
                    case SET:
                        if (!CommonEnum.SET.value().equals(event.getSubType())) {
                            return;
                        }
                    default:
                }
                Map<Class<?>, Object> params = new HashMap<>(16);
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
        val parameterTypes = handlerMethod.getMethod().getParameterTypes();
        val objects = new Object[parameterTypes.length];
        Arrays.stream(parameterTypes).forEach(InternalUtils.consumerWithIndex((item, index) -> {
            if (params.containsKey(item)) {
                objects[index] = params.remove(item);
            } else {
                objects[index] = null;
            }
        }));
        try {
            handlerMethod.getMethod().invoke(handlerMethod.getObject(), objects);
        } catch (Exception e) {
            e.printStackTrace();
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
                Map<Class<?>, Object> params = new HashMap<>(16);
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
    private Map<Class<?>, Object> matcher(String cmd, String msg) {
        Map<Class<?>, Object> params = new HashMap<>(16);
        if (!CommonEnum.DEFAULT_CMD.value().equals(cmd)) {
            val matcher = RegexUtils.regexMatcher(cmd, msg);
            if (matcher == null) {
                return null;
            }
            params.put(Matcher.class, matcher);
        }
        return params;
    }

}
