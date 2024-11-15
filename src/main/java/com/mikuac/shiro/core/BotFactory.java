package com.mikuac.shiro.core;

import com.mikuac.shiro.annotation.common.Order;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.AnnotationScanner;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.model.HandlerMethod;
import com.mikuac.shiro.properties.ShiroProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
public class BotFactory {

    private static       Set<Class<?>> annotations = new LinkedHashSet<>();
    private static final ReentrantLock lock        = new ReentrantLock();

    private final Condition          condition;
    private final ActionHandler      actionHandler;
    private final ShiroProperties    shiroProps;
    private final ApplicationContext applicationContext;

    @Autowired
    public BotFactory(
            ActionHandler actionHandler, ShiroProperties shiroProps, ApplicationContext applicationContext
    ) {
        this.actionHandler = actionHandler;
        this.shiroProps = shiroProps;
        this.applicationContext = applicationContext;
        lock.lock();
        condition = lock.newCondition();
        lock.unlock();
    }

    @PostConstruct
    public void init() {
        // 等待 Spring 容器初始化完成解锁
        lock.lock();
        condition.signalAll();
        lock.unlock();
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
            return annotations;
        }
        annotations = new AnnotationScanner().scan("com.mikuac.shiro.annotation");
        return annotations;
    }

    private MultiValueMap<Class<? extends Annotation>, HandlerMethod> getAnnotationHandler() {
        if (!annotationHandler.isEmpty()) {
            return annotationHandler;
        }
        lock.lock();
        try {
            //noinspection ResultOfMethodCallIgnored
            condition.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        lock.unlock();

        // 获取 Spring 容器中所有指定类型的对象
        Map<String, Object> beans = new HashMap<>(applicationContext.getBeansWithAnnotation(Shiro.class));
        beans.values().forEach(obj -> {
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(obj);
            Arrays.stream(targetClass.getMethods()).forEach(method -> {
                HandlerMethod handlerMethod = new HandlerMethod();
                handlerMethod.setMethod(method);
                handlerMethod.setType(targetClass);
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
        return new Bot(selfId, session, actionHandler, shiroProps.getPluginList(), getAnnotationHandler(), shiroProps.getInterceptor());
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
