package com.mikuac.shiro.handler.injection;

import com.mikuac.shiro.annotation.*;
import com.mikuac.shiro.bean.HandlerMethod;
import com.mikuac.shiro.bean.MsgChainBean;
import com.mikuac.shiro.common.utils.RegexUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.dto.event.message.WholeMessageEvent;
import com.mikuac.shiro.dto.event.notice.GroupAdminNoticeEvent;
import com.mikuac.shiro.dto.event.notice.GroupDecreaseNoticeEvent;
import com.mikuac.shiro.dto.event.notice.GroupIncreaseNoticeEvent;
import com.mikuac.shiro.enums.AtEnum;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

/**
 * InjectionHandler
 *
 * @author meme
 */
@Component
public class InjectionHandler {

    /**
     * 入群提醒
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupIncreaseNoticeEvent}
     */
    public void invokeGroupIncrease(@NotNull Bot bot, @NotNull GroupIncreaseNoticeEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        setArgs(handlers.get(GroupIncreaseHandler.class), bot, event);
    }

    /**
     * 退群提醒
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupDecreaseNoticeEvent}
     */
    public void invokeGroupDecrease(@NotNull Bot bot, @NotNull GroupDecreaseNoticeEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        setArgs(handlers.get(GroupDecreaseHandler.class), bot, event);
    }

    /**
     * 监听全部消息
     *
     * @param bot   {@link Bot}
     * @param event {@link WholeMessageEvent}
     */
    public void invokeWholeMessage(@NotNull Bot bot, @NotNull WholeMessageEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> handlerMethods = handlers.get(MessageHandler.class);
        if (handlerMethods != null && handlerMethods.size() > 0) {
            for (HandlerMethod handlerMethod : handlerMethods) {
                MessageHandler mh = handlerMethod.getMethod().getAnnotation(MessageHandler.class);
                if ("group".equals(event.getMessageType())) {
                    if (checkAt(event.getArrayMsg(), event.getSelfId(), mh.at())) {
                        continue;
                    }
                }
                Map<Class<?>, Object> argsMap = matcher(mh.cmd(), event.getMessage());
                if (argsMap == null) {
                    continue;
                }
                argsMap.put(Bot.class, bot);
                argsMap.put(WholeMessageEvent.class, event);
                invokeMethod(handlerMethod, argsMap);
            }
        }
    }

    /**
     * at 检查
     *
     * @param arrayMsg 消息链
     * @param selfId   机器人QQ
     * @param at       at枚举
     * @return boolean
     */
    private boolean checkAt(List<MsgChainBean> arrayMsg, long selfId, AtEnum at) {
        List<Long> atList = ShiroUtils.getAtList(arrayMsg);
        if (at == AtEnum.NEED && !atList.contains(selfId)) {
            return true;
        }
        return at == AtEnum.NOT_NEED && atList.contains(selfId);
    }

    /**
     * 群聊消息
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupMessageEvent}
     */
    public void invokeGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> handlerMethods = handlers.get(GroupMessageHandler.class);
        if (handlerMethods != null && handlerMethods.size() > 0) {
            for (HandlerMethod handlerMethod : handlerMethods) {
                GroupMessageHandler gmh = handlerMethod.getMethod().getAnnotation(GroupMessageHandler.class);
                if (checkAt(event.getArrayMsg(), event.getSelfId(), gmh.at())) {
                    continue;
                }
                Map<Class<?>, Object> argsMap = matcher(gmh.cmd(), event.getMessage());
                if (argsMap == null) {
                    continue;
                }
                argsMap.put(Bot.class, bot);
                argsMap.put(GroupMessageEvent.class, event);
                invokeMethod(handlerMethod, argsMap);
            }
        }
    }

    /**
     * 私聊消息
     *
     * @param bot   {@link Bot}
     * @param event {@link PrivateMessageEvent}
     */
    public void invokePrivateMessage(@NotNull Bot bot, @NotNull PrivateMessageEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> handlerMethods = handlers.get(PrivateMessageHandler.class);
        if (handlerMethods != null && handlerMethods.size() > 0) {
            for (HandlerMethod handlerMethod : handlerMethods) {
                PrivateMessageHandler pmh = handlerMethod.getMethod().getAnnotation(PrivateMessageHandler.class);
                Map<Class<?>, Object> argsMap = matcher(pmh.cmd(), event.getMessage());
                if (argsMap == null) {
                    continue;
                }
                argsMap.put(PrivateMessageEvent.PrivateSender.class, event.getPrivateSender());
                argsMap.put(Bot.class, bot);
                argsMap.put(PrivateMessageEvent.class, event);
                invokeMethod(handlerMethod, argsMap);
            }
        }
    }

    /**
     * 管理员变动事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupAdminNoticeEvent}
     */
    public void invokeGroupAdmin(@NotNull Bot bot, @NotNull GroupAdminNoticeEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> handlerMethods = handlers.get(GroupAdminHandler.class);
        if (handlerMethods != null && handlerMethods.size() > 0) {
            for (HandlerMethod handlerMethod : handlerMethods) {
                GroupAdminHandler handler = handlerMethod.getMethod().getAnnotation(GroupAdminHandler.class);
                switch (handler.type()) {
                    case OFF:
                        continue;
                    case ALL:
                        break;
                    case UNSET:
                        if (!"unset".equals(event.getSubType())) {
                            continue;
                        }
                    case SET:
                        if (!"set".equals(event.getSubType())) {
                            continue;
                        }
                    default:
                }
                Map<Class<?>, Object> argsMap = new HashMap<>(16);
                argsMap.put(Bot.class, bot);
                argsMap.put(GroupAdminNoticeEvent.class, event);
                invokeMethod(handlerMethod, argsMap);
            }
        }
    }

    private void invokeMethod(HandlerMethod handlerMethod, Map<Class<?>, Object> argMap) {
        Class<?>[] parameterTypes = handlerMethod.getMethod().getParameterTypes();
        Object[] objects = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (argMap.containsKey(parameterType)) {
                objects[i] = argMap.remove(parameterType);
            } else {
                objects[i] = null;
            }
        }
        try {
            handlerMethod.getMethod().invoke(handlerMethod.getObject(), objects);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 参数设置
     *
     * @param handlerMethods Annotation methods
     * @param bot            {@link Bot}
     * @param event          Object
     */
    private void setArgs(List<HandlerMethod> handlerMethods, Bot bot, Object event) {
        if (handlerMethods != null && handlerMethods.size() > 0) {
            for (HandlerMethod handlerMethod : handlerMethods) {
                Map<Class<?>, Object> argsMap = new HashMap<>(16);
                argsMap.put(Bot.class, bot);
                if (event instanceof GroupIncreaseNoticeEvent) {
                    argsMap.put(GroupIncreaseNoticeEvent.class, event);
                }
                if (event instanceof GroupDecreaseNoticeEvent) {
                    argsMap.put(GroupDecreaseNoticeEvent.class, event);
                }
                invokeMethod(handlerMethod, argsMap);
            }
        }
    }

    /**
     * 返回匹配的消息Matcher类
     *
     * @param cmd 正则表达式
     * @param msg 消息内容
     * @return argsMap
     */
    private Map<Class<?>, Object> matcher(String cmd, String msg) {
        String defCommand = "none";
        Map<Class<?>, Object> argsMap = new ConcurrentHashMap<>(16);
        if (!defCommand.equals(cmd)) {
            Matcher matcher = RegexUtils.regexMatcher(cmd, msg);
            if (matcher == null) {
                return null;
            }
            argsMap.put(Matcher.class, matcher);
        }
        return argsMap;
    }

}
