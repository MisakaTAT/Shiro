package com.mikuac.shiro.common.utils;

/**
 * @author meme
 * @version V0.0.1
 * @Package com.mikuac.shiro.injection
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

}
