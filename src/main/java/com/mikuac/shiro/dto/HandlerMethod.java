package com.mikuac.shiro.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;


@Getter
@Setter
public class HandlerMethod {

    private Class<?> type;

    private Object object;

    private Method method;

}
