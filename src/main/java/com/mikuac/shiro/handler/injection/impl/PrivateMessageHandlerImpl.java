package com.mikuac.shiro.handler.injection.impl;

import com.mikuac.shiro.dto.action.anntation.PrivateMessageHandler;
import com.mikuac.shiro.common.utils.ArrayUtils;
import com.mikuac.shiro.common.utils.RegexUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.handler.injection.BaseHandler;
import org.springframework.util.MultiValueMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author meme
 * @version V0.0.1
 * @Package com.mikuac.shiro.handler.injection
 * @Description:
 * @date 2021/10/26 21:24
 */

public class PrivateMessageHandlerImpl implements BaseHandler {

    public  void invokeEvent(Bot bot, PrivateMessageEvent event){
        //获取所有成员方法
//        Method[] methods = pluginClass.getMethods();

        MultiValueMap<Class<? extends Annotation>, Method> methods = bot.getMethods();
        List<Method> methodList = methods.get(PrivateMessageHandler.class);
        for (Method m:methodList){
                PrivateMessageHandler pmh=m.getAnnotation(PrivateMessageHandler.class);

                if (pmh.excludeSenderIds().length>0&&ArrayUtils.contain(pmh.excludeSenderIds(),event.getUserId()))
                    return;

                if (pmh.excludeSenderIds().length>0&&!ArrayUtils.contain(pmh.senderIds(),event.getUserId()))
                    return;
                Map<Class<?>,Object> argMap= new HashMap<>();
                if (!"none".equals(pmh.regex())){
                    Matcher matcher = RegexUtils.regexMacher(pmh.regex(), event.getRawMessage());
                    if (matcher==null)
                        return;
                    argMap.put(Matcher.class,matcher);
                }





                argMap.put(PrivateMessageEvent.PrivateSender.class,event.getPrivateSender());

                argMap.put(Bot.class,bot);
                argMap.put(PrivateMessageEvent.class,event) ;
                Class<?>[] parameterTypes = m.getParameterTypes();
                Object[] objects = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class<?> parameterType = parameterTypes[i];
                    if (argMap.containsKey(parameterType)){
                        objects[i]=argMap.remove(parameterType);
                    }
                }
                try {
                    m.invoke(m.getDeclaringClass(),objects);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

        }
    }
}
