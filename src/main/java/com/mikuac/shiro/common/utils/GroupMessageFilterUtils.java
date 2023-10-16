package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class GroupMessageFilterUtils {

    private GroupMessageFilterUtils() {
    }

    private static final ConcurrentHashMap<String, Long> CACHE = new ConcurrentHashMap<>();

    // 插入消息并指定缓存时间
    public static boolean insertMessage(GroupMessageEvent messageEvent, int cacheTime) {
        String md5 = DigestUtils.md5DigestAsHex(messageEvent.getRawMessage().getBytes(StandardCharsets.UTF_8));
        long now = System.currentTimeMillis();
        removeExpiredMessageId(now);
        if (CACHE.containsKey(md5)) {
            long cacheNow = CACHE.get(md5);
            if (cacheNow + cacheTime >= now) {
                return false;
            }
        }
        CACHE.put(md5, now + cacheTime);
        return true;
    }

    // 超出缓存时间后清理缓存
    public static void removeExpiredMessageId(long time) {
        CACHE.entrySet().removeIf(entry -> entry.getValue() < time);
    }

}
