package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.util.DigestUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class GroupMessageFilterUtils {

    private GroupMessageFilterUtils() {
    }

    private static final ConcurrentHashMap<String, Long> CACHE = new ConcurrentHashMap<>();

    // 插入消息并指定缓存时间
    public static boolean insertMessage(GroupMessageEvent messageEvent, int cacheTime) {
        // 消息缓存
        byte[] messageByte = messageEvent.getRawMessage().getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(messageByte.length + 16);
        buffer.put(messageByte);
        buffer.putLong(messageEvent.getGroupId());
        buffer.putLong(messageEvent.getSender().getUserId());
        // 针对发送群以及发送者加盐, 防止误伤复读消息
        String md5 = DigestUtils.md5DigestAsHex(buffer.array());
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
