package com.mikuac.shiro.annotation;

import java.lang.annotation.*;

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
}
