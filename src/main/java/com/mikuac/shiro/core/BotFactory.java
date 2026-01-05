package com.mikuac.shiro.core;

import com.mikuac.shiro.annotation.common.Order;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.AnnotationScanner;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.model.HandlerMethod;
import com.mikuac.shiro.properties.ShiroProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketSession;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 2021/7/7.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Slf4j
@Component
public class BotFactory implements ApplicationListener<ContextRefreshedEvent> {

    private static Set<Class<?>> annotations = new LinkedHashSet<>();
    private final ActionHandler actionHandler;
    private final ShiroProperties shiroProps;
    private final ApplicationContext applicationContext;
    private final AnnotationMethodContainer annotationMethodContainer = new AnnotationMethodContainer();

    @Autowired
    public BotFactory(
            ActionHandler actionHandler, ShiroProperties shiroProps, ApplicationContext applicationContext
    ) {
        this.actionHandler = actionHandler;
        this.shiroProps = shiroProps;
        this.applicationContext = applicationContext;
    }

    /**
     * 获取所有注解
     *
     * @return 注解集合
     */
    private static Set<Class<?>> getAnnotations() {
        if (!annotations.isEmpty()) {
            log.debug("Using cached annotations, size: {}", annotations.size());
            return annotations;
        }
        log.debug("Scanning for annotations in package: com.mikuac.shiro.annotation");
        annotations = new AnnotationScanner().scan("com.mikuac.shiro.annotation");
        log.debug("Found {} annotations", annotations.size());
        return annotations;
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        log.debug("Starting to collect beans with @Shiro annotation");
        Map<String, Object> beans = new HashMap<>(applicationContext.getBeansWithAnnotation(Shiro.class));
        log.debug("Found {} beans with @Shiro annotation", beans.size());
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> annotationHandler = extractMethods(beans);
        log.debug("Starting handler method sorting");
        this.sort(annotationHandler);
        log.debug("Handler methods sorted, total size: {}", annotationHandler.size());
        this.annotationMethodContainer.setAnnotationHandler(annotationHandler);
    }

    private MultiValueMap<Class<? extends Annotation>, HandlerMethod> extractMethods(Map<String, Object> beans) {
        MultiValueMap<Class<? extends Annotation>, HandlerMethod> result = new LinkedMultiValueMap<>();
        beans.values().forEach(obj -> {
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(obj);
            log.debug("Processing class: {}", targetClass.getName());
            Arrays.stream(targetClass.getMethods()).forEach(method -> {
                HandlerMethod handlerMethod = new HandlerMethod();
                handlerMethod.setMethod(method);
                handlerMethod.setType(targetClass);
                handlerMethod.setObject(obj);
                log.debug("Processing method: {}.{}", targetClass.getSimpleName(), method.getName());
                Arrays.stream(method.getDeclaredAnnotations()).forEach(annotation -> {
                    Set<Class<?>> as = getAnnotations();
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    if (as.contains(annotationType)) {
                        log.debug("Adding handler for annotation: {} on method: {}.{}", annotationType.getSimpleName(), targetClass.getSimpleName(), method.getName());
                        result.add(annotation.annotationType(), handlerMethod);
                    }
                });
            });
        });
        return result;
    }

    /**
     * 创建Bot对象
     *
     * @param selfId  机器人账号
     * @param session {@link WebSocketSession}
     * @return {@link Bot}
     */
    public Bot createBot(long selfId, WebSocketSession session) {
        log.debug("Bot instance creation started: {}", selfId);
        log.debug("Using WebSocket session: {}", session.getId());
        log.debug("Plugin list size: {}", shiroProps.getPluginList().size());
        Bot bot = new Bot(selfId, session, actionHandler, shiroProps.getPluginList(), annotationMethodContainer, shiroProps.getInterceptor());
        log.debug("Bot instance created successfully for ID: {}", selfId);
        return bot;
    }

    /**
     * 优先级排序
     *
     * @param annotationHandler 处理方法集合Map
     */
    private void sort(MultiValueMap<Class<? extends Annotation>, HandlerMethod> annotationHandler) {
        if (annotationHandler.isEmpty()) {
            log.debug("No handlers to sort");
            return;
        }
        // 排序
        log.debug("Starting to sort handlers by @Order annotation");
        annotationHandler.keySet().forEach(annotation -> {
            log.debug("Sorting handlers for annotation: {}", annotation.getSimpleName());
            List<HandlerMethod> handlers = annotationHandler.get(annotation);
            handlers = handlers.stream().distinct().sorted(
                    Comparator.comparing(
                            handlerMethod -> {
                                Order order = handlerMethod.getMethod().getAnnotation(Order.class);
                                int orderValue = Optional.ofNullable(order == null ? null : order.value()).orElse(Integer.MAX_VALUE);
                                log.debug("Method: {}.{} has order value: {}", handlerMethod.getType().getSimpleName(), handlerMethod.getMethod().getName(), orderValue);
                                return orderValue;
                            }
                    )
            ).collect(Collectors.toCollection(ArrayList::new));
            log.debug("Sorted {} handlers for annotation: {}", handlers.size(), annotation.getSimpleName());
            annotationHandler.put(annotation, handlers);
            log.debug("Handler sorting completed");
        });
    }

    @Getter
    @Setter
    @SuppressWarnings("java:S3077")
    public static class AnnotationMethodContainer {
        // 以注解为键，存放包含此注解的处理方法
        private volatile MultiValueMap<Class<? extends Annotation>, HandlerMethod> annotationHandler = new LinkedMultiValueMap<>();
    }
}
