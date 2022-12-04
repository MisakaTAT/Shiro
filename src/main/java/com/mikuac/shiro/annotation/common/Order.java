package com.mikuac.shiro.annotation.common;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 独立的优先级注解，可作用于监听函数上。
 *
 * @author ilxyil
 * @version $Id: $Id
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {

    int value() default Integer.MAX_VALUE;
}