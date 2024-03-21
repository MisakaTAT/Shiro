package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.util.DigestUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class GroupMessageFilterUtils {

    private GroupMessageFilterUtils() {
    }

    private static final ConcurrentHashMap<String, Long> CACHE = new ConcurrentHashMap<>();

    // 插入消息并指定缓存时间
    public static boolean insertMessage(GroupMessageEvent messageEvent, int cacheTime) {
        // 消息缓存

        String messageKey = String.valueOf(messageEvent.getTime()) +
                // 针对发送群以及发送者加盐, 防止误伤复读消息
                messageEvent.getGroupId() +
                messageEvent.getUserId();
        long now = System.currentTimeMillis();
        removeExpiredMessageId(now);
        if (CACHE.containsKey(messageKey)) {
            long cacheNow = CACHE.get(messageKey);
            if (cacheNow + cacheTime >= now) {
                return false;
            }
        }
        CACHE.put(messageKey, now + cacheTime);
        return true;
    }

    // 超出缓存时间后清理缓存
    public static void removeExpiredMessageId(long time) {
        CACHE.entrySet().removeIf(entry -> entry.getValue() < time);
    }

}
