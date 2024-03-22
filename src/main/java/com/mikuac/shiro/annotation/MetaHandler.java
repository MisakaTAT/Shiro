package com.mikuac.shiro.annotation;

import com.mikuac.shiro.enums.MetaEventEnum;

import java.lang.annotation.*;

/***
 * 使用@MetaHandler 仅支持参数(Bot bot, HeartbeatMetaEvent event) 或 (Bot bot, LifecycleMetaEvent event)
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MetaHandler {
    MetaEventEnum type() default MetaEventEnum.HEARTBEAT;
}
