package com.mikuac.shiro.common.utils;

import java.lang.annotation.Annotation;

/**
 * @author meme
 * @version V0.0.1
 * @Package com.mikuac.shiro.handler.injection
 * @Description:
 * @date 2021/10/26 21:24
 */

public class ArrayUtils {

    public static boolean contain(long[] sources, long value) {

        for (long source : sources) {
            if (source == value) {
                return true;
            }
        }
        return false;
    }

    public static boolean contain(Long[] sources, Long value) {
        for (Long source : sources) {
            if (source.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contain(Object[] sources, Object value) {
        for (Object source : sources) {
            if (source == value) {
                return true;
            }
        }
        return false;
    }


    public static boolean containAnnotation(Annotation[] sources, Class<Annotation> value) {
        for (Annotation source : sources) {
            if (source.getClass()== value) {
                return true;
            }
        }
        return false;
    }
}
