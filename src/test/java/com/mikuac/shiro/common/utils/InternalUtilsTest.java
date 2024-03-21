package com.mikuac.shiro.common.utils;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InternalUtilsTest {

    @Test
    void consumerWithIndexTest() {
        val list = Arrays.asList(1, 2, 3);
        list.forEach(InternalUtils.consumerWithIndex((item, index) -> assertEquals(item, list.get(index))));
    }

}
