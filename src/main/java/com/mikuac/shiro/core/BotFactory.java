package com.mikuac.shiro.core;


import com.mikuac.shiro.dto.HandlerMethod;
import com.mikuac.shiro.dto.action.annotation.GroupAdminHandler;
import com.mikuac.shiro.dto.action.annotation.GroupMessageHandler;
import com.mikuac.shiro.dto.action.annotation.GroupUploadHandler;
import com.mikuac.shiro.dto.action.annotation.PrivateMessageHandler;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.properties.PluginProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.*;

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
     * @param selfId  Bot QQ
     * @param session websocket session
     * @return Bot对象
     */
    public Bot createBot(long selfId, WebSocketSession session) {
        Map<String, BotPlugin> beansOfType = appContext.getBeansOfType(BotPlugin.class);

//        HandlerMethodCollection handlerMethodCollection =new HandlerMethodCollection();

        MultiValueMap<Class<? extends Annotation>, HandlerMethod> handlers= new LinkedMultiValueMap<>();


        for (Object bean : beansOfType.values()) {
            Class<?> beanClass = bean.getClass();
            Arrays.stream(beanClass.getMethods()).forEach(method ->
            {      HandlerMethod handlerMethod=new HandlerMethod();
                    handlerMethod.setMethod(method);
                    handlerMethod.setType(beanClass);
                    handlerMethod.setObject(bean);
                if (method.isAnnotationPresent(PrivateMessageHandler.class)){
                    handlers.add(PrivateMessageHandler.class,handlerMethod);
                }
                if (method.isAnnotationPresent(GroupMessageHandler.class)){
                    handlers.add(GroupMessageHandler.class,handlerMethod);

                }

                if (method.isAnnotationPresent(GroupAdminHandler.class)){
                    handlers.add(GroupAdminHandler.class,handlerMethod);

                }

                if (method.isAnnotationPresent(GroupUploadHandler.class)){
                    handlers.add(GroupUploadHandler.class,handlerMethod);

                }
            }
            );
        }


        return new Bot(selfId, session, actionHandler, pluginProperties.getPluginList(),handlers);
    }


}
