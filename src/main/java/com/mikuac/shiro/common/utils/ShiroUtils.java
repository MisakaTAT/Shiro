package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2021/8/10.
 *
 * @author Zero
 * @version $Id: $Id
 */
@SuppressWarnings({"unused", "squid:S1192", "Duplicates"})
public class ShiroUtils {

    private ShiroUtils() {
    }

    /**
     * 判断是否为全体at
     *
     * @param msg 消息
     * @return 是否为全体at
     */
    public static boolean isAtAll(String msg) {
        return msg.contains("[CQ:at,qq=all]");
    }

    /**
     * 判断是否为全体at
     *
     * @param arrayMsg 消息链
     * @return 是否为全体at
     */
    public static boolean isAtAll(List<ArrayMsg> arrayMsg) {
        return arrayMsg.stream().anyMatch(it -> it.getType().equals(MsgTypeEnum.at) && it.getLongData("qq") == 0L);
    }

    /**
     * 获取消息内所有at对象账号（不包含全体 at）
     *
     * @param arrayMsg 消息链
     * @return at对象列表
     */
    public static List<Long> getAtList(List<ArrayMsg> arrayMsg) {
        return arrayMsg
                .stream()
                .filter(it -> MsgTypeEnum.at == it.getType() && it.getLongData("qq") != 0L)
                .map(it -> it.getLongData("qq"))
                .toList();
    }

    /**
     * 获取消息内所有图片链接
     *
     * @param arrayMsg 消息链
     * @return 图片链接列表
     */
    public static List<String> getMsgImgUrlList(List<ArrayMsg> arrayMsg) {
        return arrayMsg
                .stream()
                .filter(it -> MsgTypeEnum.image == it.getType())
                .map(it -> it.getStringData("url"))
                .toList();
    }

    /**
     * 获取消息内所有视频链接
     *
     * @param arrayMsg 消息链
     * @return 视频链接列表
     */
    public static List<String> getMsgVideoUrlList(List<ArrayMsg> arrayMsg) {
        return arrayMsg
                .stream()
                .filter(it -> MsgTypeEnum.video == it.getType())
                .map(it -> it.getStringData("url"))
                .toList();
    }

    /**
     * 获取群头像
     *
     * @param groupId 群号
     * @param size    头像尺寸
     * @return 头像链接 （size为0返回真实大小, 40(40*40), 100(100*100), 640(640*640)）
     */
    public static String getGroupAvatar(long groupId, int size) {
        return String.format("https://p.qlogo.cn/gh/%s/%s/%s", groupId, groupId, size);
    }

    /**
     * 获取用户头像
     *
     * @param userId QQ号
     * @param size   头像尺寸
     * @return 头像链接 （size为0返回真实大小, 40(40*40), 100(100*100), 640(640*640)）
     */
    public static String getUserAvatar(long userId, int size) {
        return String.format("https://q2.qlogo.cn/headimg_dl?dst_uin=%s&spec=%s", userId, size);
    }

    /**
     * 消息解码
     *
     * @param string 需要解码的内容
     * @return 解码处理后的字符串
     */
    public static String unescape(String string) {
        return string
                .replace("&#44;", ",")
                .replace("&#91;", "[")
                .replace("&#93;", "]")
                .replace("&amp;", "&");
    }

    /**
     * 消息编码
     *
     * @param string 需要编码的内容
     * @return 编码处理后的字符串
     */
    public static String escape(String string) {
        return string
                .replace("&", "&amp;")
                .replace(",", "&#44;")
                .replace("[", "&#91;")
                .replace("]", "&#93;");
    }

    /**
     * 消息编码（可用于转义CQ码，防止文本注入）
     *
     * @param string 需要编码的内容
     * @return 编码处理后的字符串
     */
    public static String escape2(String string) {
        return string
                .replace("[", "&#91;")
                .replace("]", "&#93;");
    }

    /**
     * 创建自定义消息合并转发
     *
     * @param uin      发送者QQ号
     * @param name     发送者显示名字
     * @param contents 消息列表，每个元素视为一个消息节点
     *                 <a href="https://docs.go-cqhttp.org/cqcode/#%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91">参考文档</a>
     * @return 消息结构
     */
    public static List<Map<String, Object>> generateForwardMsg(long uin, String name, List<String> contents) {
        List<Map<String, Object>> nodes = new ArrayList<>();
        contents.forEach(msg -> {
            Map<String, Object> node = new HashMap<>();
            node.put("type", "node");
            Map<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("uin", uin);
            data.put("content", msg);
            node.put("data", data);
            nodes.add(node);
        });
        return nodes;
    }

    /**
     * 兼容 Shamrock
     * 生成自定义合并转发消息
     *
     * @param contents 消息列表，每个元素视为一个消息节点
     * @return 消息结构
     */
    public static List<Map<String, Object>> generateForwardMsg(List<String> contents) {
        List<Map<String, Object>> nodes = new ArrayList<>();
        contents.forEach(msg -> {
            Map<String, Object> node = new HashMap<>();
            node.put("type", "node");
            Map<String, Object> data = new HashMap<>();
            data.put("content", msg);
            node.put("data", data);
            nodes.add(node);
        });
        return nodes;
    }

    /**
     * 兼容 Lagrange
     * 生成自定义合并转发消息
     *
     * @param contents 消息列表，每个元素视为一个消息节点 Object 可为 List<ArrayMsg> 或 CQCode
     * @return 消息结构
     */
    public static List<Map<String, Object>> generateForwardMsg(String uin, String name, List<?> contents) {
        List<Map<String, Object>> nodes = new ArrayList<>();
        contents.forEach(msg -> {
            Map<String, Object> node = new HashMap<>();
            node.put("type", "node");
            Map<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("uin", uin);
            data.put("content", msg);
            node.put("data", data);
            nodes.add(node);
        });
        return nodes;
    }

    /**
     * 生成自定义合并转发消息的单条内容
     *
     * @param uin  指定QQ号，用于头像的显示
     * @param name 指定的显示的QQ昵称
     * @param msg  消息内容
     * @return 消息结构
     * <p>使用 {@link com.mikuac.shiro.core.Bot#sendGroupForwardMsg(long, List, String, String, String, List)}和{@link com.mikuac.shiro.core.Bot#sendPrivateForwardMsg(long, List, String, String, String, List)}来发送生成后的聊天记录</p>
     */
    public static Map<String, Object> generateSingleMsg(long uin, String name, String msg) {

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("uin", uin);
        data.put("content", msg);

        Map<String, Object> node = new HashMap<>();
        node.put("type", "node");
        node.put("data", data);

        return node;
    }

    /**
     * 兼容 Lagrange
     * 生成自定义合并转发消息
     *
     * @param contents 消息列表，每个元素视为一个消息节点 Object 可为 List<ArrayMsg> 或 CQCode
     * @return 消息结构
     */
    public static List<Map<String, Object>> generateForwardMsg(Bot bot, List<?> contents) {
        List<Map<String, Object>> nodes = new ArrayList<>();
        contents.forEach(msg -> {
            Map<String, Object> node = new HashMap<>();
            node.put("type", "node");
            Map<String, Object> data = new HashMap<>();
            data.put("name", bot.getLoginInfo().getData().getNickname());
            String appName = bot.getVersionInfo().getData().getAppName();
            // 兼容Lagrange
            if (appName.equals("Lagrange.OneBot")) {
                data.put("uin", String.valueOf(bot.getSelfId()));
            } else {
                data.put("uin", bot.getSelfId());
            }
            data.put("content", msg);
            node.put("data", data);
            nodes.add(node);
        });
        return nodes;
    }

    /**
     * 兼容 Shamrock
     * 生成引用消息和自定义消息混合合并转发
     *
     * @param contents   消息列表，每个元素视为一个消息节点
     * @param quoteMsgId 引用的消息ID
     * @return 消息结构
     */
    public static List<Map<String, Object>> generateForwardMsg(List<String> contents, List<String> quoteMsgId) {
        List<Map<String, Object>> nodes = generateForwardMsg(contents);
        quoteMsgId.forEach(id -> {
            Map<String, Object> node = new HashMap<>();
            node.put("type", "node");
            Map<String, Object> data = new HashMap<>();
            data.put("id", id);
            node.put("data", data);
            nodes.add(node);
        });
        return nodes;
    }

}
