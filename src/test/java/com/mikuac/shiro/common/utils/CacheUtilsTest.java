package com.mikuac.shiro.common.utils;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CacheUtilsTest {

    @Test
    void testCacheUtils() {
        long[] groups = {987654321, 123456789};
        val cache = new CacheUtils();
        assertFalse(Arrays.binarySearch(groups, 123456789) >= 0);
        assertTrue(Arrays.binarySearch(cache.getSortedGroups(groups), 123456789) >= 0);
    }

}
