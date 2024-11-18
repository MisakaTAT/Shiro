package com.mikuac.shiro.core;

import com.mikuac.shiro.annotation.common.Order;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.AnnotationScanner;
import com.mikuac.shiro.exception.ShiroException;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.model.HandlerMethod;
import com.mikuac.shiro.properties.ShiroProperties;
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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
    private static final ReentrantLock lock = new ReentrantLock();

    private final Condition condition;
    private final ActionHandler actionHandler;
    private final ShiroProperties shiroProps;
    private final ApplicationContext applicationContext;
    private boolean loading = true;
    private boolean isActionHandlerLoaded = false;

    @Autowired
    public BotFactory(
            ActionHandler actionHandler, ShiroProperties shiroProps, ApplicationContext applicationContext
    ) {
        this.actionHandler = actionHandler;
        this.shiroProps = shiroProps;
        this.applicationContext = applicationContext;
        this.condition = lock.newCondition();
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        // 等待 Spring 容器初始化完成解锁
        lock.lock();
        condition.signal();
        lock.unlock();
        loading = false;
    }

    // 以注解为键，存放包含此注解的处理方法
    MultiValueMap<Class<? extends Annotation>, HandlerMethod> annotationHandler = new LinkedMultiValueMap<>();

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

    private MultiValueMap<Class<? extends Annotation>, HandlerMethod> getAnnotationHandler() {
        // 双重检查锁机制避免重复加载
        if (isActionHandlerLoaded) {
            log.debug("Using cached annotation handlers, size: {}", annotationHandler.size());
            return annotationHandler;
        }

        while (loading) {
            try {
                log.debug("Waiting for annotation handlers to be loaded");
                boolean signaled = condition.await(10, TimeUnit.SECONDS);
                if (!signaled) {
                    log.warn("Condition await timed out while waiting for annotation handlers to load");
                    throw new IllegalStateException("Failed to load annotation handlers within timeout");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ShiroException(e);
            }
        }

        // 解锁后再次检查是否已经加载
        if (isActionHandlerLoaded) {
            return annotationHandler;
        }

        log.debug("Starting to collect beans with @Shiro annotation");
        Map<String, Object> beans = new HashMap<>(applicationContext.getBeansWithAnnotation(Shiro.class));
        log.debug("Found {} beans with @Shiro annotation", beans.size());

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
                        annotationHandler.add(annotation.annotationType(), handlerMethod);
                    }
                });
            });
        });

        log.debug("Starting handler method sorting");
        this.sort(annotationHandler);

        // 标记加载完成
        isActionHandlerLoaded = true;

        // 当 ioc 加载完毕后仅解锁一个线程，这个加载完毕后再解锁其他所有的线程，避免重复加载。
        lock.lock();
        condition.signal();
        lock.unlock();

        log.debug("Handler methods sorted, total size: {}", annotationHandler.size());
        return annotationHandler;
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
        Bot bot = new Bot(selfId, session, actionHandler, shiroProps.getPluginList(), getAnnotationHandler(), shiroProps.getInterceptor());
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
            ).toList();
            log.debug("Sorted {} handlers for annotation: {}", handlers.size(), annotation.getSimpleName());
            annotationHandler.put(annotation, handlers);
            log.debug("Handler sorting completed");
        });
    }
}
