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
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

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
                if (CommonEnum.GROUP.value().equals(event.getMessageType()) && checkAt(event.getArrayMsg(), event.getSelfId(), annotation.at())) {
                    return;
                }
                Map<Class<?>, Object> params = matcher(annotation.cmd(), extractMsg(event.getMessage(), event.getArrayMsg(), annotation.at()));
                if (params.isEmpty()) {
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
    @SuppressWarnings("squid:S3776")
    private boolean checkAt(List<ArrayMsg> arrayMsg, long selfId, AtEnum atEnum) {
        String all = "all";

        if (atEnum == AtEnum.OFF) {
            return false;
        }

        if (arrayMsg.isEmpty()) {
            return true;
        }

        if (atEnum == AtEnum.NEED) {
            ArrayMsg atObj = arrayMsg.get(0);
            String atALL = atObj.getData().get("qq");
            if (MsgTypeEnum.at != atObj.getType()) {
                return true;
            }
            if (atALL == null || atALL.isEmpty()) {
                return false;
            }
            if (all.equals(atALL)) {
                return true;
            }
            long atUserId = Long.parseLong(atALL);
            return selfId != atUserId;
        }

        if (atEnum == AtEnum.NOT_NEED) {
            ArrayMsg atObj = arrayMsg.get(0);
            String atUserIdStr = atObj.getData().get("qq");
            if (atUserIdStr == null || atUserIdStr.isEmpty()) {
                return false;
            }
            if (all.equals(atUserIdStr)) {
                return false;
            }
            long atUserId = Long.parseLong(atUserIdStr);
            return selfId == atUserId;
        }

        return true;
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
                if (checkAt(event.getArrayMsg(), Long.parseLong(event.getSelfTinyId()), annotation.at())) {
                    return;
                }
                Map<Class<?>, Object> params = matcher(annotation.cmd(), extractMsg(event.getMessage(), event.getArrayMsg(), annotation.at()));
                if (params.isEmpty()) {
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
    private String extractMsg(String message, List<ArrayMsg> arrayMsg, AtEnum atEnum) {
        String msg = message;
        if (atEnum == AtEnum.NEED) {
            String atCode = ShiroUtils.arrayMsgToCode(arrayMsg.get(0));
            msg = msg.replace(atCode, "").replace(" ", "");
        }
        return msg;
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
                if (checkAt(event.getArrayMsg(), event.getSelfId(), annotation.at())) {
                    return;
                }
                Map<Class<?>, Object> params = matcher(annotation.cmd(), extractMsg(event.getMessage(), event.getArrayMsg(), annotation.at()));
                if (params.isEmpty()) {
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
                if (params.isEmpty()) {
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
    private Map<Class<?>, Object> matcher(String cmd, String msg) {
        Map<Class<?>, Object> params = new HashMap<>();
        if (!CommonEnum.DEFAULT_CMD.value().equals(cmd)) {
            Matcher matcher = RegexUtils.regexMatcher(cmd, msg);
            if (matcher == null) {
                return Collections.emptyMap();
            }
            params.put(Matcher.class, matcher);
        }
        return params;
    }

}
