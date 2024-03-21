package com.mikuac.shiro.common.utils;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ScanUtilsTest {

    @Test
    void scanAnnotationTest() {
        val annotations = new ScanUtils().scanAnnotation("com.mikuac.shiro.annotation");
        annotations.forEach(a -> assertTrue(a.isAnnotation()));
    }

}
