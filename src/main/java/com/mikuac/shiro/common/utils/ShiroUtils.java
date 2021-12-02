package com.mikuac.shiro.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mikuac.shiro.bean.MsgChainBean;
import com.mikuac.shiro.enums.ShiroUtilsEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 2021/8/10.
 *
 * @author Zero
 */
public class ShiroUtils {

    /**
     * 判断是否为全体at
     *
     * @param msg 消息
     * @return 值
     */
    public static boolean isAtAll(String msg) {
        return msg.contains(ShiroUtilsEnum.AT_ALL_CQ_CODE.getValue());
    }

    /**
     * 获取消息内所有at对象账号
     *
     * @param arrayMsg 消息链
     * @return at对象列表
     */
    public static List<Long> getAtList(List<MsgChainBean> arrayMsg) {
        List<Long> atList = new ArrayList<>();
        arrayMsg.stream().filter(it ->
                "at".equals(it.getType()) && !"all".equals(it.getData().get("qq"))
        ).forEach(it -> atList.add(Long.parseLong(it.getData().get("qq"))));
        return atList;
    }

    /**
     * 获取消息内所有图片链接
     *
     * @param arrayMsg 消息链
     * @return 图片链接列表
     */
    public static List<String> getMsgImgUrlList(List<MsgChainBean> arrayMsg) {
        List<String> imgUrlList = new ArrayList<>();
        arrayMsg.stream().filter(it ->
                "image".equals(it.getType())
        ).forEach(it -> imgUrlList.add(it.getData().get("url")));
        return imgUrlList;
    }

    /**
     * 获取消息内所有视频链接
     *
     * @param arrayMsg 消息链
     * @return 视频链接列表
     */
    public static List<String> getMsgVideoUrlList(List<MsgChainBean> arrayMsg) {
        List<String> imgUrlList = new ArrayList<>();
        arrayMsg.stream().filter(it ->
                "video".equals(it.getType())
        ).forEach(it -> imgUrlList.add(it.getData().get("url")));
        return imgUrlList;
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
        return string.replace("&#44;", ",")
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
        return string.replace("&", "&amp;")
                .replace(",", "&#44;")
                .replace("[", "&#91;")
                .replace("]", "&#93;");
    }

    /**
     * array 消息上报转消息链
     *
     * @param msg 需要修改客户端消息上报类型为 array
     * @return 消息链
     */
    public static List<MsgChainBean> arrayToMsgChain(String msg) {
        return JSONObject.parseArray(msg, MsgChainBean.class);
    }

    /**
     * string 消息上报转消息链
     * 建议传入 event.getMessage 而非 event.getRawMessage
     * 例如 gocqhttp rawMessage 不包含图片 url
     *
     * @param msg 需要修改客户端消息上报类型为 string
     * @return 消息链
     */
    public static List<MsgChainBean> stringToMsgChain(String msg) {
        String splitRegex = "(?<=\\[CQ:[^]]{0,}])|(?=\\[CQ:[^]]+])";
        String cqCodeCheckRegex = "\\[CQ:(?:[^,\\[\\]]+)(?:(?:,[^,=\\[\\]]+=[^,\\[\\]]*)*)]";
        JSONArray array = new JSONArray();
        for (String s1 : msg.split(splitRegex)) {
            if (s1.isEmpty()) {
                continue;
            }
            if (s1.matches(cqCodeCheckRegex)) {
                s1 = s1.substring(1, s1.length() - 1);
            }
            JSONObject object = new JSONObject();
            JSONObject params = new JSONObject();
            if (!s1.startsWith("CQ:")) {
                object.put("type", "text");
                params.put("text", s1);
            } else {
                String[] s2 = s1.split(",");
                object.put("type", s2[0].substring(s2[0].indexOf(":") + 1));
                Arrays.stream(s2).filter(it ->
                        !it.startsWith("CQ:")
                ).forEach(it -> {
                    String key = it.substring(0, it.indexOf("="));
                    String value = ShiroUtils.unescape(it.substring(it.indexOf("=") + 1));
                    params.put(key, value);
                });
            }
            object.put("data", params);
            array.add(object);
        }
        return array.toJavaList(MsgChainBean.class);
    }

}