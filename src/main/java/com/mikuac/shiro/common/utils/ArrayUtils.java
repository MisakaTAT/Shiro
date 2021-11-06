package com.mikuac.shiro.common.utils;

import java.lang.annotation.Annotation;

/**
 * ArrayUtils
 *
 * @author meme
 */
public class ArrayUtils {

    /**
     * @param sources 数组
     * @param value   值
     * @return 是否存在
     */
    public static boolean contain(long[] sources, long value) {
        for (long source : sources) {
            if (source == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param sources 数组
     * @param value   值
     * @return 是否存在
     */
    public static boolean contain(Long[] sources, Long value) {
        for (Long source : sources) {
            if (source.equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param sources 数组
     * @param value   值
     * @return 是否存在
     */
    public static boolean contain(Object[] sources, Object value) {
        for (Object source : sources) {
            if (source == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param sources 数组
     * @param value   值
     * @return 是否存在
     */
    public static boolean containAnnotation(Annotation[] sources, Class<Annotation> value) {
        for (Annotation source : sources) {
            if (source.getClass() == value) {
                return true;
            }
        }
        return false;
    }

}
