package com.mikuac.shiro.core;

import com.mikuac.shiro.annotation.common.Order;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.AopTargetUtils;
import com.mikuac.shiro.common.utils.ScanUtils;
import com.mikuac.shiro.exception.ShiroException;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.model.HandlerMethod;
import com.mikuac.shiro.properties.ShiroProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketSession;

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

    private ActionHandler actionHandler;

    @Autowired
    public void setActionHandler(ActionHandler actionHandler) {
        this.actionHandler = actionHandler;
    }

    private ShiroProperties shiroProperties;

    @Autowired
    public void setShiroProperties(ShiroProperties shiroProperties) {
        this.shiroProperties = shiroProperties;
    }

    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

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
     * @param session {@link WebSocketSession}
     * @return {@link Bot}
     */
    public Bot createBot(long selfId, WebSocketSession session) {
        
        log.debug("Start creating bot instance: {}", selfId);
        // 获取 Spring 容器中所有指定类型的对象
        Map<String, Object> beans = new HashMap<>(applicationContext.getBeansWithAnnotation(Shiro.class));
        // 一键多值 注解为 Key 存放所有包含某个注解的方法
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> annotationHandler = new LinkedMultiValueMap<>();
        beans.values().forEach(obj -> {
            Object target;
            try {
                target = AopTargetUtils.getTarget(obj);
            } catch (Exception e) {
                throw new ShiroException(e);
            }
            Class<?> beanClass = target.getClass();
            Arrays.stream(beanClass.getMethods()).forEach(method -> {
                HandlerMethod handlerMethod = new HandlerMethod();
                handlerMethod.setMethod(method);
                handlerMethod.setType(beanClass);
                handlerMethod.setObject(obj);
                Arrays.stream(method.getDeclaredAnnotations()).forEach(annotation -> {
                    Set<Class<?>> as = getAnnotations();
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    if (as.contains(annotationType)) {
                        annotationHandler.add(annotation.annotationType(), handlerMethod);
                    }
                });
            });
        });
        this.sort(annotationHandler);
        return new Bot(selfId, session, actionHandler, shiroProperties.getPluginList(), annotationHandler, shiroProperties.getInterceptor());
    }

    /**
     * 优先级排序
     *
     * @param annotationHandler 处理方法集合Map
     */
    private void sort(MultiValueMap<Class<? extends Annotation>, HandlerMethod> annotationHandler) {
        if (annotationHandler.isEmpty()) {
            return;
        }
        // 排序
        annotationHandler.keySet().forEach(annotation -> {
            List<HandlerMethod> handlers = annotationHandler.get(annotation);
            handlers = handlers.stream().sorted(
                    Comparator.comparing(
                            handlerMethod -> {
                                Order order = handlerMethod.getMethod().getAnnotation(Order.class);
                                return Optional.ofNullable(order == null ? null : order.value()).orElse(Integer.MAX_VALUE);
                            }
                    )
            ).toList();
            annotationHandler.put(annotation, handlers);
        });
    }

}
