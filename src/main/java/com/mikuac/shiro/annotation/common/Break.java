package com.mikuac.shiro.annotation.common;


import java.lang.annotation.*;

/**
 * 独立的处理阻断注解，可作用于监听函数上。
 *
 * @author ilxyil
 * @version $Id: $Id
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Break {
}
