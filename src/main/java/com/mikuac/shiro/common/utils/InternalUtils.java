package com.mikuac.shiro.common.utils;

import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

/**
 * <p>InternalUtils class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
public class InternalUtils {

    private InternalUtils() {
    }

    public static <T> Consumer<T> consumerWithIndex(ObjIntConsumer<T> consumer) {
        class O {
            int i;
        }
        O object = new O();
        return item -> {
            int index = object.i++;
            consumer.accept(item, index);
        };
    }

}
