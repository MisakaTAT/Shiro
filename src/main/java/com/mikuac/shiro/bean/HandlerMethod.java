package com.mikuac.shiro.bean;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * HandlerMethod
 *
 * @author meme
 * @version V0.0.1
 * @Package com.mikuac.shiro.bean
 * @date 2021/10/26 21:24
 */

@Data
public class HandlerMethod {

    private Class<?> type;

    private Object object;

    private Method method;

}
