package com.mikuac.shiro.common.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CacheUtils {

    private final Map<Integer, long[]> groupsMap;
    private final Map<Integer, long[]> sendersMap;

    public CacheUtils() {
        this.groupsMap = new HashMap<>();
        this.sendersMap = new HashMap<>();
    }

    public long[] getSortedSenders(long[] senders) {
        return getSortedArray(senders, sendersMap);
    }

    public long[] getSortedGroups(long[] groups) {
        return getSortedArray(groups, groupsMap);
    }

    private long[] getSortedArray(long[] array, Map<Integer, long[]> map) {
        int key = Arrays.hashCode(array);
        if (map.containsKey(key)) {
            return map.get(key);
        }
        long[] sortedArray = array.clone();
        Arrays.sort(sortedArray);
        map.put(key, sortedArray);
        return sortedArray;
    }

}
