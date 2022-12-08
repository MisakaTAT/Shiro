package com.mikuac.shiro.common.utils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * <p>InternalUtils class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
public class InternalUtils {

    /**
     * <p>consumerWithIndex.</p>
     *
     * @param consumer BiConsumer
     * @param <T>      T
     * @return Consumer
     */
    public static <T> Consumer<T> consumerWithIndex(BiConsumer<T, Integer> consumer) {
        class Object {
            int i;
        }
        Object object = new Object();
        return item -> {
            int index = object.i++;
            consumer.accept(item, index);
        };
    }

}
