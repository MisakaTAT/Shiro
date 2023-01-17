package com.mikuac.shiro.common.utils;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * <p>AopTargetUtils class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
public class AopTargetUtils {

    private AopTargetUtils() {
    }

    /**
     * <p>getTarget.</p>
     *
     * @param proxy {@link Object}
     * @return TargetObject
     * @throws Exception exception
     */
    public static Object getTarget(Object proxy) throws Exception {
        if (!AopUtils.isAopProxy(proxy)) {
            return proxy;
        }
        // 判断是 jdk 还是 cglib 代理
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            proxy = getJdkDynamicProxyTargetObject(proxy);
        } else {
            proxy = getCglibProxyTargetObject(proxy);
        }
        return getTarget(proxy);
    }

    /**
     * @param proxy {@link Object}
     * @return CglibProxyTargetObject
     * @throws Exception exception
     */
    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        ReflectionUtils.makeAccessible(h);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        ReflectionUtils.makeAccessible(advised);
        return ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
    }

    /**
     * @param proxy {@link Object}
     * @return JdkDynamicProxyTargetObject
     * @throws Exception exception
     */
    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        ReflectionUtils.makeAccessible(h);
        AopProxy aopProxy = (AopProxy) h.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        ReflectionUtils.makeAccessible(advised);
        return ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
    }

}
