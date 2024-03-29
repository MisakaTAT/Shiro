package com.mikuac.shiro.model;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * HandlerMethod
 *
 * @author meme
 * @version $Id: $Id
 */
@Data
public class HandlerMethod {

    private Class<?> type;

    private Object object;

    private Method method;

}
