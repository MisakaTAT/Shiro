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

    /**
     * 用户白名单（仅响应白名单内用户的消息）
     *
     * @return 用户白名单数组
     */
    long[] userWhiteList() default {};

    /**
     * 用户黑名单（仅响应不在黑名单内用户的消息）
     *
     * @return 群组黑名单数组
     */
    long[] userBlackList() default {};

}
