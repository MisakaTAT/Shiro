package com.mikuac.shiro.annotation;

import java.lang.annotation.*;

/**
 * 频道消息事件注解
 *
 * @author Alexskim
 * @version $Id: $Id
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GuildMessageHandler {

}
