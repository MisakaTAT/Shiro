package com.mikuac.shiro.core;


import com.google.common.base.CaseFormat;
import com.mikuac.shiro.common.utils.ArrayUtils;
import com.mikuac.shiro.dto.action.anntation.GroupMessageHandler;
import com.mikuac.shiro.dto.action.anntation.PrivateMessageHandler;
import com.mikuac.shiro.dto.action.common.HandlerMethod;
import com.mikuac.shiro.dto.action.common.HandlerMethodCollection;
import com.mikuac.shiro.enums.MethodEnum;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.properties.PluginProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created on 2021/7/7.
 *
 * @author Zero
 */
@Component
public class BotFactory {

    @Resource
    private ActionHandler actionHandler;

    @Resource
    private PluginProperties pluginProperties;


    private static final Map<String, List<HandlerMethod>> handlerMethodMap=new HashMap<>();;



   @Resource
    private ApplicationContext appContext;

    /**
     * 创建Bot对象
     *
     * @param selfId  Bot QQ
     * @param session websocket session
     * @return Bot对象
     */
    public Bot createBot(long selfId, WebSocketSession session) {
        Map<String, BotPlugin> beansOfType = appContext.getBeansOfType(BotPlugin.class);

//        HandlerMethodCollection handlerMethodCollection =new HandlerMethodCollection();

        MultiValueMap<Class<? extends Annotation>, Method> methods= new LinkedMultiValueMap<>();


        for (Object bean : beansOfType.values()) {
            Class<?> beanClass = bean.getClass();
            Arrays.stream(beanClass.getMethods()).forEach(method ->
            {
                if (method.isAnnotationPresent(PrivateMessageHandler.class)){
                    methods.add(PrivateMessageHandler.class,method);
                }
                if (method.isAnnotationPresent(GroupMessageHandler.class)){
                    methods.add(GroupMessageHandler.class,method);

                }}
            );
        }


        return new Bot(selfId, session, actionHandler, pluginProperties.getPluginList(),methods);
    }

    public static Set<HandlerMethod> getHandlerMethodListByAnnotation(String botName, Predicate<? super HandlerMethod> predicate) {
        List<HandlerMethod> handlerMethods = handlerMethodMap.get(botName);
        if (handlerMethods == null || handlerMethods.isEmpty()) {
            return new HashSet<>();
        }
        return handlerMethods.stream().filter(predicate).collect(Collectors.toSet());
    }

}
