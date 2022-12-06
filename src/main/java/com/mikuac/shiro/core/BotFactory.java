package com.mikuac.shiro.core;

import com.mikuac.shiro.annotation.Shiro;
import com.mikuac.shiro.annotation.common.Break;
import com.mikuac.shiro.annotation.common.Order;
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
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
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
        log.debug("Bot instance creating {}", selfId);
        // 获取 Spring 容器中所有指定类型的对象
        val beans = new HashMap<String, Object>(16);
        beans.putAll(applicationContext.getBeansWithAnnotation(Shiro.class));
        // 一键多值 注解为 Key 存放所有包含某个注解的方法
        val annotationHandler = new LinkedMultiValueMap<Class<? extends Annotation>, HandlerMethod>();
        for (Object object : beans.values()) {
            Object target;
            try {
                target = AopTargetUtils.getTarget(object);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            val beanClass = target.getClass();
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
        this.sortAndBlock(annotationHandler);
        return new Bot(selfId, session, actionHandler, shiroProperties.getPluginList(), annotationHandler, shiroProperties.getInterceptor());
    }

    /**
     * 处理优先级次序以及阻断
     *
     * @param annotationHandler 处理方法集合Map
     * @return
     */
    private void sortAndBlock(LinkedMultiValueMap<Class<? extends Annotation>, HandlerMethod> annotationHandler) {

        //为空直接返回
        if (annotationHandler.isEmpty()) {
            return;
        }

        //排序及阻断
        annotationHandler.keySet().forEach(annotation -> {
            List<HandlerMethod> handlers = annotationHandler.get(annotation);

            //排序
            handlers = handlers.stream().sorted(
                    Comparator.comparing(
                            handlerMethod -> Optional.of(
                                    handlerMethod.getMethod().getAnnotation(Order.class).value()
                            ).orElse(Integer.MAX_VALUE))
            ).collect(Collectors.toList());

            //阻断（相同优先级各凭运气）
            for (int i = 0; i < handlers.size(); i++) {
                Break breakAnnotation = handlers.get(i).getMethod().getAnnotation(Break.class);
                if (breakAnnotation != null) {
                    handlers = handlers.subList(0, i + 1);
                    break;
                }
            }
            annotationHandler.put(annotation, handlers);
        });
    }

}
