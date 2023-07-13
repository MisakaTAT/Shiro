package com.mikuac.shiro.common.utils;

import java.util.concurrent.ConcurrentHashMap;

public class GroupMessageFilterUtil {
    private static final ConcurrentHashMap<Integer, Long> CACHE = new ConcurrentHashMap<>();

    // 插入消息并指定缓存时间
    public static boolean insertMessageId(Integer messageId, int cacheTime) {
        long now = System.currentTimeMillis();
        removeExpiredMessageId(now);
        if (CACHE.containsKey(messageId)) {
            long cacheNow = CACHE.get(messageId);
            if (cacheNow + cacheTime >= now) {
                return false;
            }
            return false;
        }
        CACHE.put(messageId, now + cacheTime);
        return true;
    }
    // 超出缓存时间后清理缓存
    public static void removeExpiredMessageId(long time) {
        CACHE.entrySet()
                .removeIf(entry -> entry.getValue() < time);
    }
}
