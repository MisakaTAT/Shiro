package com.mikuac.shiro.annotation;

import java.lang.annotation.*;

/**
 * 私聊事件注解
 *
 * @author meme
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrivateMessageHandler {

    /**
     * 触发命令，支持正则
     *
     * @return 正则表达式
     */
    String cmd() default "none";

}
