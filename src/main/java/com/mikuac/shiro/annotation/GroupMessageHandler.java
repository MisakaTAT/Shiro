package com.mikuac.shiro.annotation;

import com.mikuac.shiro.enums.AtEnum;

import java.lang.annotation.*;

/**
 * 群消息事件注解
 *
 * @author meme
 * @version V0.0.1
 * @Package com.mikuac.shiro.annotation
 * @date 2021/10/26 21:24
 */

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GroupMessageHandler {

    /**
     * 触发命令，支持正则
     *
     * @return 正则表达式
     */
    String cmd() default "none";

    /**
     * 群组白名单（仅响应白名单内群组的消息）
     *
     * @return 群白名单数组
     */
    long[] groupWhiteList() default {};

    /**
     * 群组黑名单（仅响应不在黑名单内群组的消息）
     *
     * @return 群黑名单数组
     */
    long[] groupBlackList() default {};

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

    /**
     * 检查是否被at
     * 如果值为 NEED 只处理带有at机器人的消息
     * 如果值为 NOT_NEED 若消息中at了机器人此条消息会被忽略
     *
     * @return at枚举
     */
    AtEnum at() default AtEnum.OFF;

}
