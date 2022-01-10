package com.mikuac.shiro.core;

import com.mikuac.shiro.bean.HandlerMethod;
import com.mikuac.shiro.common.utils.AopTargetUtils;
import com.mikuac.shiro.common.utils.ScanUtils;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.properties.PluginProperties;
import lombok.val;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created on 2021/7/7.
 *
 * @author Zero
 */
@Component
public class BotFactory {

    private static Set<Class<?>> annotations = new LinkedHashSet<>();

    @Resource
    private ActionHandler actionHandler;

    @Resource
    private PluginProperties pluginProperties;

    @Resource
    private ApplicationContext appContext;

    /**
     * 获取所有注解类
     *
     * @return 注解集合
     */
    private static Set<Class<?>> getAnnotations() {
        if (annotations.size() != 0) {
            return annotations;
        }
        annotations = new ScanUtils().scanAnnotation("com.mikuac.shiro.annotation");
        return annotations;
    }

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
            Object target;
            try {
                target = AopTargetUtils.getTarget(bean);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Class<?> beanClass = target.getClass();
            Arrays.stream(beanClass.getMethods()).forEach(method ->
                    {
                        val handlerMethod = new HandlerMethod();
                        handlerMethod.setMethod(method);
                        handlerMethod.setType(beanClass);
                        handlerMethod.setObject(bean);
                        Arrays.stream(method.getDeclaredAnnotations()).forEach(annotation -> {
                            val annotations = getAnnotations();
                            val annotationType = annotation.annotationType();
                            if (annotations.contains(annotationType)) {
                                annotationHandler.add(annotation.annotationType(), handlerMethod);
                            }
                        });
                    }
            );
        }
        return new Bot(selfId, session, actionHandler, pluginProperties.getPluginList(), annotationHandler);
    }

}
