package com.mikuac.shiro.common.utils;

import com.alibaba.fastjson2.JSON;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.NonNull;

import java.util.*;

public class MessageConverser {

    private MessageConverser() {
    }

    public static String arrayToString(ArrayMsg arrayMsg) {
        StringBuilder builder = new StringBuilder();
        if (Objects.isNull(arrayMsg.getType()) || MsgTypeEnum.unknown.equals(arrayMsg.getType())) {
            builder.append("[CQ:").append(MsgTypeEnum.unknown);
        } else {
            builder.append("[CQ:").append(arrayMsg.getType());
        }
        arrayMsg.getData().forEach((k, v) -> builder.append(",").append(k).append("=").append(ShiroUtils.escape(v)));
        builder.append("]");
        return builder.toString();
    }

    public static String arraysToString(List<ArrayMsg> array) {
        StringBuilder builder = new StringBuilder();
        for (ArrayMsg item : array) {
            if (!MsgTypeEnum.text.equals(item.getType())) {
                builder.append("[CQ:").append(item.getType());
                // message 字段转回 CQ 码的时候不要转义，raw_message 会保留原始内容。
                item.getData().forEach((k, v) -> builder.append(",").append(k).append("=").append(v));
                builder.append("]");
            } else {
                builder.append(ShiroUtils.escape(item.getData().get(MsgTypeEnum.text.toString())));
            }
        }
        return builder.toString();
    }

    @SuppressWarnings({"java:S6541", "java:S3776"})
    public static List<ArrayMsg> stringToArray(@NonNull String msg) {
        if (msg.isEmpty()) {
            return new ArrayList<>();
        }

        List<ArrayMsg> chain = new ArrayList<>();
        int len = msg.length();
        int i = 0;

        while (i < len) {
            if (msg.charAt(i) == '[' && i + 1 < len && msg.charAt(i + 1) == 'C' &&
                    i + 2 < len && msg.charAt(i + 2) == 'Q' &&
                    i + 3 < len && msg.charAt(i + 3) == ':') {
                // 找到 CQ 码开始位置
                int start = i;
                i += 4; // 跳过 "[CQ:"

                // 解析 CQ 码类型
                StringBuilder typeBuilder = new StringBuilder();
                while (i < len && msg.charAt(i) != ',' && msg.charAt(i) != ']') {
                    typeBuilder.append(msg.charAt(i));
                    i++;
                }

                if (i >= len) {
                    // 格式错误，当作普通文本处理
                    addTextMsg(chain, msg.substring(start, len));
                    break;
                }

                String type = typeBuilder.toString();
                Map<String, String> data = new HashMap<>();

                // 解析参数
                if (msg.charAt(i) == ',') {
                    i++; // 跳过逗号
                    while (i < len && msg.charAt(i) != ']') {
                        // 解析键
                        StringBuilder keyBuilder = new StringBuilder();
                        while (i < len && msg.charAt(i) != '=' && msg.charAt(i) != ']') {
                            keyBuilder.append(msg.charAt(i));
                            i++;
                        }

                        if (i >= len || msg.charAt(i) == ']') {
                            break;
                        }

                        i++; // 跳过等号

                        // 解析值
                        StringBuilder valueBuilder = new StringBuilder();
                        while (i < len && msg.charAt(i) != ',' && msg.charAt(i) != ']') {
                            valueBuilder.append(msg.charAt(i));
                            i++;
                        }

                        String key = keyBuilder.toString();
                        String value = ShiroUtils.unescape(valueBuilder.toString());
                        if (!key.isEmpty()) {
                            data.put(key, value);
                        }

                        if (i < len && msg.charAt(i) == ',') {
                            i++; // 跳过逗号继续解析下一个参数
                        }
                    }
                }

                if (i < len && msg.charAt(i) == ']') {
                    i++; // 跳过结束括号
                    // 创建 ArrayMsg
                    ArrayMsg item = new ArrayMsg();
                    item.setType(MsgTypeEnum.typeOf(type));
                    item.setData(data);
                    chain.add(item);
                } else {
                    // 格式错误，当作普通文本处理
                    addTextMsg(chain, msg.substring(start, i));
                }
            } else {
                // 普通文本
                StringBuilder textBuilder = new StringBuilder();
                while (i < len && !(msg.charAt(i) == '[' && i + 3 < len &&
                        msg.startsWith("[CQ:", i))) {
                    textBuilder.append(msg.charAt(i));
                    i++;
                }
                String text = textBuilder.toString();
                if (!text.isEmpty()) {
                    addTextMsg(chain, text);
                }
            }
        }
        return chain;
    }

    private static void addTextMsg(List<ArrayMsg> chain, String text) {
        if (text.isEmpty()) {
            return;
        }
        // 检查最后一个消息是否为文本类型，如果是则合并
        if (!chain.isEmpty()) {
            ArrayMsg lastMsg = chain.get(chain.size() - 1);
            if (lastMsg.getType() == MsgTypeEnum.text) {
                lastMsg.getData().compute("text", (k, existingText) -> existingText + ShiroUtils.unescape(text));
                return;
            }
        }
        // 创建新的文本消息
        ArrayMsg item = new ArrayMsg();
        item.setType(MsgTypeEnum.text);
        Map<String, String> data = new HashMap<>();
        data.put("text", ShiroUtils.unescape(text));
        item.setData(data);
        chain.add(item);
    }

    public static void convert(@NonNull String msg, MessageEvent event) {
        // 如果 msg 是一个有效的 json 数组则作为 array 上报
        if (JSON.isValidArray(msg)) {
            List<ArrayMsg> arrayMsg = JSON.parseArray(msg, ArrayMsg.class);
            // 将 array 转换回 string
            event.setArrayMsg(arrayMsg);
            event.setMessage(arraysToString(arrayMsg));
            return;
        }
        // string 上报
        event.setArrayMsg(stringToArray(msg));
    }

}
