package com.mikuac.shiro.handler.injection;

import com.mikuac.shiro.annotation.GroupAdminHandler;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.PrivateMessageHandler;
import com.mikuac.shiro.bean.HandlerMethod;
import com.mikuac.shiro.common.utils.ArrayUtils;
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
 * @version V0.0.1
 * @Package com.mikuac.shiro.handler.injection
 * @date 2021/10/26 21:24
 */

@Component
public class InjectionHandler {

    /**
     * 群聊消息
     *
     * @param bot   Bot 对象
     * @param event GroupMessageEvent 类
     */
    public void invokeGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> handlerMethodList = handlers.get(GroupMessageHandler.class);
        if (handlerMethodList == null) {
            return;
        }
        for (HandlerMethod handlerMethod : handlerMethodList) {
            GroupMessageHandler gmh = handlerMethod.getMethod().getAnnotation(GroupMessageHandler.class);
            if (!groupCheck(gmh.groupBlackList(), gmh.groupWhiteList(), event.getGroupId())) {
                return;
            }
            if (!userCheck(gmh.userBlackList(), gmh.userWhiteList(), event.getUserId())) {
                return;
            }
            List<String> atList = ShiroUtils.getAtList(event.getRawMessage());
            val selfId = String.valueOf(event.getSelfId());
            if (gmh.at() == AtEnum.NEED && !atList.contains(selfId)) {
                return;
            }
            if (gmh.at() == AtEnum.NOT_NEED && atList.contains(selfId)) {
                return;
            }
            Map<Class<?>, Object> argsMap = matcher(gmh.cmd(), event.getRawMessage());
            if (argsMap == null) {
                return;
            }
            argsMap.put(Bot.class, bot);
            argsMap.put(GroupMessageEvent.class, event);
            invokeMethod(handlerMethod, argsMap);
        }
    }

    /**
     * 私聊消息
     *
     * @param bot   Bot 对象
     * @param event PrivateMessageEvent 类
     */
    public void invokePrivateMessage(@NotNull Bot bot, @NotNull PrivateMessageEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> handlerMethods = handlers.get(PrivateMessageHandler.class);
        if (handlerMethods == null) {
            return;
        }
        for (HandlerMethod handlerMethod : handlerMethods) {
            PrivateMessageHandler pmh = handlerMethod.getMethod().getAnnotation(PrivateMessageHandler.class);
            if (!userCheck(pmh.userBlackList(), pmh.userWhiteList(), event.getUserId())) {
                return;
            }
            Map<Class<?>, Object> argsMap = matcher(pmh.cmd(), event.getRawMessage());
            if (argsMap == null) {
                return;
            }
            argsMap.put(PrivateMessageEvent.PrivateSender.class, event.getPrivateSender());
            argsMap.put(Bot.class, bot);
            argsMap.put(PrivateMessageEvent.class, event);
            invokeMethod(handlerMethod, argsMap);
        }
    }

    public void invokeGroupAdmin(@NotNull Bot bot, @NotNull GroupAdminNoticeEvent event) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers = bot.getAnnotationHandler();
        List<HandlerMethod> handlerMethods = handlers.get(GroupAdminHandler.class);
        if (handlerMethods == null) {
            return;
        }
        for (HandlerMethod handlerMethod : handlerMethods) {
            GroupAdminHandler handler = handlerMethod.getMethod().getAnnotation(GroupAdminHandler.class);
            if (handler.groupBlackList().length > 0 && ArrayUtils.contain(handler.groupBlackList(), event.getGroupId())) {
                return;
            }
            if (handler.groupWhiteList().length > 0 && !ArrayUtils.contain(handler.groupWhiteList(), event.getGroupId())) {
                return;
            }
            switch (handler.type()) {
                case OFF:
                    return;
                case ALL:
                    break;
                case UNSET:
                    if (!"unset".equals(event.getSubType())) {
                        return;
                    }
                case SET:
                    if (!"set".equals(event.getSubType())) {
                        return;
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

    private boolean groupCheck(long[] groupBlackList, long[] groupWhiteList, long groupId) {
        // 黑名单不为空，且存在于黑名单内
        if (groupBlackList.length > 0 && ArrayUtils.contain(groupBlackList, groupId)) {
            return false;
        }
        // 白名单不为空，且不存在于白名单内
        return groupWhiteList.length <= 0 || ArrayUtils.contain(groupWhiteList, groupId);
    }

    private boolean userCheck(long[] userBlackList, long[] userWhiteList, long userId) {
        // 黑名单不为空，且存在于黑名单内
        if (userBlackList.length > 0 && ArrayUtils.contain(userBlackList, userId)) {
            return false;
        }
        // 白名单不为空，且不存在于白名单内
        return userWhiteList.length <= 0 || ArrayUtils.contain(userWhiteList, userId);
    }

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
