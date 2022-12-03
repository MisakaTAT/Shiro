package com.mikuac.shiro.core;

import com.mikuac.shiro.annotation.Shiro;
import com.mikuac.shiro.bean.HandlerMethod;
import com.mikuac.shiro.common.utils.AopTargetUtils;
import com.mikuac.shiro.common.utils.ScanUtils;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.properties.ShiroProperties;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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
 * @version $Id: $Id
 */
@Slf4j
@Component
public class BotFactory {

    private static Set<Class<?>> annotations = new LinkedHashSet<>();

    @Resource
    private ActionHandler actionHandler;

    @Resource
    private ShiroProperties shiroProperties;

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 获取所有注解类
     *
     * @return 注解集合
     */
    private static Set<Class<?>> getAnnotations() {
        if (!annotations.isEmpty()) {
            return annotations;
        }
        annotations = new ScanUtils().scanAnnotation("com.mikuac.shiro.annotation");
        return annotations;
    }

    /**
     * 创建Bot对象
     *
     * @param selfId  机器人账号
     * @param session {@link org.springframework.web.socket.WebSocketSession}
     * @return {@link com.mikuac.shiro.core.Bot}
     */
    public Bot createBot(long selfId, WebSocketSession session) {
        log.debug("Start creating bot instance {}", selfId);
        // 获取 Spring 容器中所有指定类型的对象
        Map<String, Object> beans = new HashMap<>(16);
        beans.putAll(applicationContext.getBeansWithAnnotation(Shiro.class));
        // 一键多值 注解为 Key 存放所有包含某个注解的方法
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> annotationHandler = new LinkedMultiValueMap<>();
        for (Object object : beans.values()) {
            Object target;
            try {
                target = AopTargetUtils.getTarget(object);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Class<?> beanClass = target.getClass();
            Arrays.stream(beanClass.getMethods()).forEach(method -> {
                val handlerMethod = new HandlerMethod();
                handlerMethod.setMethod(method);
                handlerMethod.setType(beanClass);
                handlerMethod.setObject(object);
                Arrays.stream(method.getDeclaredAnnotations()).forEach(annotation -> {
                    val annotations = getAnnotations();
                    val annotationType = annotation.annotationType();
                    if (annotations.contains(annotationType)) {
                        annotationHandler.add(annotation.annotationType(), handlerMethod);
                    }
                });
            });
        }
        return new Bot(selfId, session, actionHandler, shiroProperties.getPluginList(), annotationHandler, shiroProperties.getInterceptor());
    }

}
