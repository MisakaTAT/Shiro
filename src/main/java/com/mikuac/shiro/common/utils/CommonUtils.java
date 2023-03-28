package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.enums.AtEnum;
import com.mikuac.shiro.enums.CommonEnum;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

/**
 * @author zero
 * @version $Id: $Id
 */
public class CommonUtils {

    private CommonUtils() {
    }

    /**
     * @param arrayMsg 消息链
     * @param selfId   机器人QQ
     * @param at       {@link AtEnum}
     * @return 是否通过检查
     */
    public static boolean atCheck(List<ArrayMsg> arrayMsg, long selfId, AtEnum at) {
        Optional<ArrayMsg> opt = Optional.ofNullable(parseAt(arrayMsg, selfId));
        return switch (at) {
            case NEED -> opt.map(item -> {
                long target = Long.parseLong(item.getData().get("qq"));
                return target == 0L || target != selfId;
            }).orElse(true);
            case NOT_NEED -> opt.map(item -> {
                long target = Long.parseLong(item.getData().get("qq"));
                return target == selfId;
            }).orElse(false);
            default -> false;
        };
    }

    /**
     * 提取去除@后的消息内容
     *
     * @param msg      原始消息
     * @param arrayMsg {@link List} of {@link ArrayMsg}
     * @param atEnum   {@link AtEnum}
     * @return 处理后的消息
     */
    public static String extractMsg(String msg, List<ArrayMsg> arrayMsg, AtEnum atEnum, long selfId) {
        if (atEnum != AtEnum.NEED) {
            return msg;
        }
        ArrayMsg item = parseAt(arrayMsg, selfId);
        if (item != null) {
            String code = ShiroUtils.arrayMsgToCode(arrayMsg.get(arrayMsg.indexOf(item)));
            return msg.replace(code, "").trim();
        }
        return msg;
    }

    /**
     * @param arrayMsg {@link List} of {@link ArrayMsg}
     * @param selfId   机器人账号
     * @return {@link ArrayMsg}
     */
    public static ArrayMsg parseAt(List<ArrayMsg> arrayMsg, long selfId) {
        if (arrayMsg.isEmpty()) {
            return null;
        }
        int index = 0;
        ArrayMsg item = arrayMsg.get(index);
        String rawTarget = item.getData().getOrDefault("qq", "0");
        long target = Long.parseLong(CommonEnum.AT_ALL.value().equals(rawTarget) ? "0" : rawTarget);
        index = arrayMsg.size() - 1;
        if ((target == 0L || target != selfId) && index >= 0) {
            item = arrayMsg.get(index);
            // @ 右侧可能会有空格
            index = arrayMsg.size() - 2;
            if (MsgTypeEnum.text == item.getType() && index >= 0) {
                item = arrayMsg.get(index);
            }
            rawTarget = item.getData().getOrDefault("qq", "0");
            target = Long.parseLong(CommonEnum.AT_ALL.value().equals(rawTarget) ? "0" : rawTarget);
            if (target == 0L || target != selfId) {
                return null;
            }
        }
        return item;
    }

    /**
     * 返回匹配的消息 Matcher 类
     *
     * @param cmd 正则表达式
     * @param msg 消息内容
     * @return params
     */
    public static Map<Class<?>, Object> matcher(String cmd, String msg) {
        if (!CommonEnum.DEFAULT_CMD.value().equals(cmd)) {
            Optional<Matcher> matcher = RegexUtils.matcher(cmd, msg);
            return matcher.map(it -> {
                Map<Class<?>, Object> params = new HashMap<>();
                params.put(Matcher.class, matcher);
                return params;
            }).orElse(new HashMap<>());
        }
        return new HashMap<>();
    }

}
