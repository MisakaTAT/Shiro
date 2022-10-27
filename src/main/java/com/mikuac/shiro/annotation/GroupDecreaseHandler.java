package com.mikuac.shiro.annotation;

import java.lang.annotation.*;

/**
 * 退群事件注解
 *
 * @author zero
 * @version $Id: $Id
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GroupDecreaseHandler {
}
