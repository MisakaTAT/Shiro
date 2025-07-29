package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.enums.AtEnum;
import com.mikuac.shiro.enums.CommonEnum;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.enums.ReplyEnum;
import com.mikuac.shiro.exception.ShiroException;
import com.mikuac.shiro.model.ArrayMsg;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zero
 * @version $Id: $Id
 */
public class CommonUtils {

    public static final String CMD_DEFAULT_VALUE = "";

    private static final CacheUtils cache = new CacheUtils();

    private CommonUtils() {
    }

    /**
     * @param arrayMsg 消息链
     * @param selfId   机器人QQ
     * @param at       {@link AtEnum}
     * @return 是否通过检查 true: 未通过(不符合)
     */
    public static boolean atCheck(List<ArrayMsg> arrayMsg, long selfId, AtEnum at) {
        Optional<ArrayMsg> opt = Optional.ofNullable(atParse(arrayMsg, selfId));
        return switch (at) {
            case NEED -> opt.map(item -> {
                long target = item.getLongData("qq");
                return target == 0L || target != selfId;
            }).orElse(true);
            case NOT_NEED -> opt.map(item -> {
                long target = item.getLongData("qq");
                return target == selfId;
            }).orElse(false);
            default -> false;
        };
    }

    /**
     * 对消息过滤
     *
     * @param event  消息事件
     * @param selfId 机器人 QQ
     * @param filter 过滤器
     * @return 是否通过校验, true: 满足所有过滤条件, 全部通过
     */
    public static CheckResult allFilterCheck(MessageEvent event, long selfId, MessageHandlerFilter filter) {
        CheckResult result = filterCheck(event, selfId, filter);
        if (filter.invert()) {
            result.changeResult();
            // 反转后 cmd 匹配参数失效
            result.setMatcher(null);
        }
        return result;
    }

    @SuppressWarnings({"squid:S3776", "squid:S1121", "java:S6541"})
    private static CheckResult filterCheck(MessageEvent event, long selfId, MessageHandlerFilter filter) {
        Optional<Matcher> matcherOptional = Optional.empty();
        String rawMessage = msgExtract(event.getMessage(), event.getArrayMsg(), filter.at(), event.getSelfId());

        // 检查 正则
        if (!filter.cmd().isBlank() && (matcherOptional = RegexUtils.matcher(filter.cmd(), rawMessage)).isEmpty()) {
            return new CheckResult();
        }

        // 检查 @at
        if (CommonEnum.GROUP.value().equals(event.getMessageType()) && filter.at() != AtEnum.OFF && atCheck(event.getArrayMsg(), selfId, filter.at())) {
            return new CheckResult();
        }

        // 检查 reply
        if (!filter.reply().equals(ReplyEnum.OFF)) {
            Optional<ArrayMsg> reply = event.getArrayMsg().stream().filter(e -> e.getType() == MsgTypeEnum.reply).findFirst();
            boolean flag = switch (filter.reply()) {
                case OFF -> throw new ShiroException("Exception that cannot be thrown");
                case NONE -> reply.isEmpty();
                case REPLY_ALL -> reply.isPresent();
                case REPLY_ME -> reply.map(e -> e.getLongData("qq") == selfId).orElse(false);
                case REPLY_OTHER -> reply.map(e -> e.getLongData("qq") != selfId).orElse(false);
            };
            if (!flag) return new CheckResult();
        }

        // 检查包含类型
        if (filter.types().length != 0) {
            boolean flag = event
                    .getArrayMsg()
                    .stream()
                    .anyMatch(e -> Arrays.binarySearch(filter.types(), e.getType()) >= 0);
            if (!flag) return new CheckResult();
        }

        // 检查群消息来源(没使用过频道消息, 所以不知道频道消息的来源如何处理)
        if (filter.groups().length != 0 && CommonEnum.GROUP.value().equals(event.getMessageType())) {
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
            boolean flag = Arrays.binarySearch(cache.getSortedGroups(filter.groups()), groupMessageEvent.getGroupId()) >= 0;
            if (!flag) return new CheckResult();
        }

        // 检查发送者
        if (filter.senders().length != 0) {
            long[] senders = filter.senders();
            Arrays.sort(senders);
            boolean flag = Arrays.binarySearch(cache.getSortedSenders(filter.senders()), event.getUserId()) >= 0;
            if (!flag) return new CheckResult();
        }

        // 前缀匹配
        if (filter.startWith().length != 0) {
            boolean flag = false;
            for (String start : filter.startWith()) {
                flag = rawMessage.startsWith(start);
                if (flag) {
                    if (matcherOptional.isEmpty()) {
                        matcherOptional = RegexUtils.matcher("(" + start + ")(.*)", rawMessage);
                    }
                    break;
                }
            }
            if (!flag) {
                return new CheckResult();
            }
        }

        // 后缀匹配
        if (filter.endWith().length != 0) {
            boolean flag = false;
            for (String end : filter.endWith()) {
                flag = rawMessage.endsWith(end);
                if (flag) {
                    if (matcherOptional.isEmpty()) {
                        matcherOptional = RegexUtils.matcher("(.*)(" + end + ")", rawMessage);
                    }
                    break;
                }
            }
            if (!flag) {
                return new CheckResult();
            }
        }

        return new CheckResult().setResult(true).setMatcher(matcherOptional.orElse(null));
    }

    /**
     * 提取去除@后的消息内容
     *
     * @param msg      原始消息
     * @param arrayMsg {@link List} of {@link ArrayMsg}
     * @param atEnum   {@link AtEnum}
     * @return 处理后的消息
     */
    public static String msgExtract(String msg, List<ArrayMsg> arrayMsg, AtEnum atEnum, long selfId) {
        if (!List.of(AtEnum.NEED, AtEnum.BOTH).contains(atEnum)) {
            return msg;
        }
        ArrayMsg item = atParse(arrayMsg, selfId);
        if (item != null) {
            String code = arrayMsg.get(arrayMsg.indexOf(item)).toCQCode();
            return msg.replace(code, "").trim();
        }
        return msg;
    }

    /**
     * @param arrayMsg {@link List} of {@link ArrayMsg}
     * @param selfId   机器人账号
     * @return {@link ArrayMsg}
     */
    public static ArrayMsg atParse(List<ArrayMsg> arrayMsg, long selfId) {
        if (arrayMsg.isEmpty()) {
            return null;
        }
        int index = 0;
        ArrayMsg item = arrayMsg.get(index);
        long target = item.getLongData("qq");
        index = arrayMsg.size() - 1;
        if ((target == 0L || target != selfId) && index >= 0) {
            item = arrayMsg.get(index);
            // @ 右侧可能会有空格
            index = arrayMsg.size() - 2;
            if (MsgTypeEnum.text == item.getType() && index >= 0) {
                item = arrayMsg.get(index);
            }
            target = item.getLongData("qq");
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
     * @return {@link Map} of {@link Matcher}
     */
    @SuppressWarnings("squid:S1168")
    public static Map<Class<?>, Object> matcher(String cmd, String msg) {
        Map<Class<?>, Object> params = new HashMap<>();
        if (!CMD_DEFAULT_VALUE.equals(cmd)) {
            Optional<Matcher> match = RegexUtils.matcher(cmd, msg);
            if (match.isEmpty()) {
                return null;
            }
            params.put(Matcher.class, match.get());
        }
        return params;
    }

    /**
     * @param msg 消息文本内容
     * @return 替换 base64 后的内容
     */
    public static String debugMsgDeleteBase64Content(String msg) {
        String regex = "base64://[a-zA-Z\\d+/=]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(msg);
        return matcher.replaceAll("(base64)");
    }

}
