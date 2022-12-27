package com.mikuac.shiro.annotation;

import com.mikuac.shiro.enums.AtEnum;

import java.lang.annotation.*;

/**
 * <p>AnyMessageHandler class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnyMessageHandler {

    /**
     * 触发命令，支持正则
     *
     * @return 正则表达式
     */
    String cmd() default "none";

    /**
     * 检查是否被at
     * 如果值为 NEED 只处理带有at机器人的消息
     * 如果值为 NOT_NEED 若消息中at了机器人此条消息会被忽略
     *
     * @return at枚举
     */
    AtEnum at() default AtEnum.OFF;

}
