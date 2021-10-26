package com.mikuac.shiro.injection;

import com.mikuac.shiro.common.anntation.PrivateMessageHandler;
import com.mikuac.shiro.common.utils.ArrayUtils;
import com.mikuac.shiro.common.utils.RegexUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author meme
 * @version V0.0.1
 * @Package com.mikuac.shiro.injection
 * @Description:
 * @date 2021/10/26 21:24
 */

public class privateMessage {

    public static void injectPrivateMessage(Bot bot, PrivateMessageEvent event,Class<? extends BotPlugin> pluginClass ){
        //获取所有成员方法
        Method[] methods = pluginClass.getMethods();

        for (Method m:methods){
            //判断成员方法是否有注解
            if (m.isAnnotationPresent(PrivateMessageHandler.class)){
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
                    m.invoke(pluginClass.getDeclaredConstructor().newInstance(),objects);
                } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
