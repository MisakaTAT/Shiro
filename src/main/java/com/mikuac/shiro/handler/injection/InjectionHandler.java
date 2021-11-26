package com.mikuac.shiro.handler.injection;

import com.mikuac.shiro.annotation.GroupAdminHandler;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.bean.HandlerMethod;
import com.mikuac.shiro.common.utils.RegexUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.dto.event.notice.GroupAdminNoticeEvent;
import com.mikuac.shiro.enums.AtEnum;
import lombok.val;
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
     * 群聊消息
     *
     * @param bot   Bot
     * @param event GroupMessageEvent
     */
    public void invokeGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> handlerMethodList = handlers.get(GroupMessageHandler.class);
        if (handlerMethodList == null) {
            return;
        }
        for (HandlerMethod handlerMethod : handlerMethodList) {
            GroupMessageHandler gmh = handlerMethod.getMethod().getAnnotation(GroupMessageHandler.class);
            List<String> atList = ShiroUtils.getAtList(event.getRawMessage());
            val selfId = String.valueOf(event.getSelfId());
            if (gmh.at() == AtEnum.NEED && !atList.contains(selfId)) {
                continue;
            }
            if (gmh.at() == AtEnum.NOT_NEED && atList.contains(selfId)) {
                continue;
            }
            Map<Class<?>, Object> argsMap = matcher(gmh.cmd(), event.getRawMessage());
            if (argsMap == null) {
                continue;
            }
            argsMap.put(Bot.class, bot);
            argsMap.put(GroupMessageEvent.class, event);
            invokeMethod(handlerMethod, argsMap);
        }
    }

    /**
     * 私聊消息
     *
     * @param bot   Bot
     * @param event PrivateMessageEvent
     */
    public void invokePrivateMessage(@NotNull Bot bot, @NotNull PrivateMessageEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> handlerMethods = handlers.get(PrivateMessageHandler.class);
        if (handlerMethods == null) {
            return;
        }
        for (HandlerMethod handlerMethod : handlerMethods) {
            PrivateMessageHandler pmh = handlerMethod.getMethod().getAnnotation(PrivateMessageHandler.class);
            Map<Class<?>, Object> argsMap = matcher(pmh.cmd(), event.getRawMessage());
            if (argsMap == null) {
                continue;
            }
            argsMap.put(PrivateMessageEvent.PrivateSender.class, event.getPrivateSender());
            argsMap.put(Bot.class, bot);
            argsMap.put(PrivateMessageEvent.class, event);
            invokeMethod(handlerMethod, argsMap);
        }
    }

    /**
     * 管理员变动事件
     *
     * @param bot   Bot
     * @param event GroupAdminNoticeEvent
     */
    public void invokeGroupAdmin(@NotNull Bot bot, @NotNull GroupAdminNoticeEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> handlerMethods = handlers.get(GroupAdminHandler.class);
        if (handlerMethods == null) {
            return;
        }
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
