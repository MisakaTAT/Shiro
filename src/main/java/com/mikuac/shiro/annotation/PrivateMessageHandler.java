package com.mikuac.shiro.annotation;

import java.lang.annotation.*;

import static com.mikuac.shiro.common.utils.CommonUtils.CMD_DEFAULT_VALUE;

/**
 * 私聊事件注解
 *
 * @author meme
 * @version $Id: $Id
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
    String cmd() default CMD_DEFAULT_VALUE;

}
