package com.mikuac.shiro.common.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.bean.MsgChainBean;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 2021/8/10.
 *
 * @author Zero
 */
@Slf4j
@SuppressWarnings("unused")
public class ShiroUtils {

    private final static String CQ_CODE_SPLIT = "(?<=\\[CQ:[^]]{1,99999}])|(?=\\[CQ:[^]]{1,99999}])";

    private final static String CQ_CODE_REGEX = "\\[CQ:([^,\\[\\]]+)((?:,[^,=\\[\\]]+=[^,\\[\\]]*)*)]";

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
    public static boolean isAtAll(List<MsgChainBean> arrayMsg) {
        return arrayMsg.stream().anyMatch(it -> "all".equals(it.getData().get("qq")));
    }

    /**
     * 获取消息内所有at对象账号（不包含全体 at）
     *
     * @param arrayMsg 消息链
     * @return at对象列表
     */
    public static List<Long> getAtList(List<MsgChainBean> arrayMsg) {
        return arrayMsg.stream().filter(it -> "at".equals(it.getType()) && !"all".equals(it.getData().get("qq"))).map(it -> Long.parseLong(it.getData().get("qq"))).collect(Collectors.toList());
    }

    /**
     * 获取消息内所有图片链接
     *
     * @param arrayMsg 消息链
     * @return 图片链接列表
     */
    public static List<String> getMsgImgUrlList(List<MsgChainBean> arrayMsg) {
        return arrayMsg.stream().filter(it -> "image".equals(it.getType())).map(it -> it.getData().get("url")).collect(Collectors.toList());
    }

    /**
     * 获取消息内所有视频链接
     *
     * @param arrayMsg 消息链
     * @return 视频链接列表
     */
    public static List<String> getMsgVideoUrlList(List<MsgChainBean> arrayMsg) {
        return arrayMsg.stream().filter(it -> "video".equals(it.getType())).map(it -> it.getData().get("url")).collect(Collectors.toList());
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
     * 获取用户昵称
     *
     * @param userId QQ号
     * @return 用户昵称
     */
    public static String getNickname(long userId) {
        val result = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            val url = new URL(String.format("https://r.qzone.qq.com/fcg-bin/cgi_get_portrait.fcg?uins=%s", userId));
            val connection = url.openConnection();
            connection.connect();
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            String nickname = result.toString().split(",")[6];
            return nickname.substring(1, nickname.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
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
        return string.replace("&#44;", ",").replace("&#91;", "[").replace("&#93;", "]").replace("&amp;", "&");
    }

    /**
     * 消息编码
     *
     * @param string 需要编码的内容
     * @return 编码处理后的字符串
     */
    public static String escape(String string) {
        return string.replace("&", "&amp;").replace(",", "&#44;").replace("[", "&#91;").replace("]", "&#93;");
    }

    /**
     * string 消息上报转消息链
     * 建议传入 event.getMessage 而非 event.getRawMessage
     * 例如 go-cq-http rawMessage 不包含图片 url
     *
     * @param msg 需要修改客户端消息上报类型为 string
     * @return 消息链
     */
    public static List<MsgChainBean> stringToMsgChain(String msg) {
        val array = new JSONArray();
        try {
            Arrays.stream(msg.split(CQ_CODE_SPLIT)).filter(s -> !s.isEmpty()).forEach(s -> {
                val matcher = RegexUtils.regexMatcher(CQ_CODE_REGEX, s);
                val object = new JSONObject();
                val params = new JSONObject();
                if (matcher == null) {
                    object.put("type", "text");
                    params.put("text", s);
                } else {
                    object.put("type", matcher.group(1));
                    Arrays.stream(matcher.group(2).split(",")).filter(args -> !args.isEmpty()).forEach(args -> {
                        val k = args.substring(0, args.indexOf("="));
                        val v = ShiroUtils.unescape(args.substring(args.indexOf("=") + 1));
                        params.put(k, v);
                    });
                }
                object.put("data", params);
                array.add(object);
            });
        } catch (Exception e) {
            log.error("String msg convert to array msg failed: {}", e.getMessage());
            return null;
        }
        return array.toList(MsgChainBean.class);
    }

    /**
     * 创建自定义消息合并转发
     *
     * @param uin     发送者QQ号
     * @param name    发送者显示名字
     * @param msgList 消息列表，每个元素视为一个消息节点
     *                https://docs.go-cqhttp.org/cqcode/#%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91
     * @return 转发消息
     */
    public static List<Map<String, Object>> generateForwardMsg(long uin, String name, List<String> msgList) {
        List<Map<String, Object>> nodeList = new ArrayList<>();
        msgList.forEach(msg -> {
            Map<String, Object> node = new HashMap<>(5);
            node.put("type", "node");
            Map<String, Object> data = new HashMap<>(5);
            data.put("name", name);
            data.put("uin", uin);
            data.put("content", msg);
            node.put("data", data);
            nodeList.add(node);
        });
        return nodeList;
    }

}