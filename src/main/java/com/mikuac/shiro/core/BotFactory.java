package com.mikuac.shiro.core;


import com.mikuac.shiro.annotation.*;
import com.mikuac.shiro.bean.HandlerMethod;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.properties.PluginProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;


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


    @Resource
    private ApplicationContext appContext;

    /**
     * 创建Bot对象
     *
     * @param selfId  机器人账号
     * @param session websocket session
     * @return Bot对象
     */
    public Bot createBot(long selfId, WebSocketSession session) {
        // 获取 Spring 容器中指定类型的所有 JavaBean 对象
        Map<String, BotPlugin> beansOfType = appContext.getBeansOfType(BotPlugin.class);
        // 一键多值 注解为 Key 存放所有包含某个注解的方法
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> annotationHandler = new LinkedMultiValueMap<>();
        for (Object bean : beansOfType.values()) {
            Class<?> beanClass = bean.getClass();
            Arrays.stream(beanClass.getMethods()).forEach(method ->
                    {
                        HandlerMethod handlerMethod = new HandlerMethod();
                        handlerMethod.setMethod(method);
                        handlerMethod.setType(beanClass);
                        handlerMethod.setObject(bean);
                        if (method.isAnnotationPresent(PrivateMessageHandler.class)) {
                            annotationHandler.add(PrivateMessageHandler.class, handlerMethod);
                        }
                        if (method.isAnnotationPresent(GroupMessageHandler.class)) {
                            annotationHandler.add(GroupMessageHandler.class, handlerMethod);
                        }
                        if (method.isAnnotationPresent(GroupAdminHandler.class)) {
                            annotationHandler.add(GroupAdminHandler.class, handlerMethod);
                        }
                        if (method.isAnnotationPresent(MessageHandler.class)) {
                            annotationHandler.add(MessageHandler.class, handlerMethod);
                        }
                        if (method.isAnnotationPresent(GroupIncreaseHandler.class)) {
                            annotationHandler.add(GroupIncreaseHandler.class, handlerMethod);
                        }
                        if (method.isAnnotationPresent(GroupDecreaseHandler.class)) {
                            annotationHandler.add(GroupDecreaseHandler.class, handlerMethod);
                        }
                    }
            );
        }
        return new Bot(selfId, session, actionHandler, pluginProperties.getPluginList(), annotationHandler);
    }

}
