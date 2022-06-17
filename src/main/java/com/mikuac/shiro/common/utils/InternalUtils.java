package com.mikuac.shiro.common.utils;

import lombok.val;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author zero
 */
public class InternalUtils {

    public static <T> Consumer<T> consumerWithIndex(BiConsumer<T, Integer> consumer) {
        class Object {
            int i;
        }
        val object = new Object();
        return item -> {
            int index = object.i++;
            consumer.accept(item, index);
        };
    }

}
