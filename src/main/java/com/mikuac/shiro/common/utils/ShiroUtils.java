package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.enums.ShiroUtilsEnum;

import java.util.ArrayList;
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
     * @param msg 消息
     * @return at对象列表
     */
    public static List<String> getAtList(String msg) {
        List<String> atList = new ArrayList<>();
        for (String i : msg.split(ShiroUtilsEnum.CQ_CODE_SPLIT.getValue())) {
            if (i.startsWith("at")) {
                atList.add(RegexUtils.regexGroup(ShiroUtilsEnum.GET_AT_USER_ID_REGEX.getValue(), i, 1));
            }
        }
        return atList;
    }

    /**
     * 获取消息内所有图片链接
     *
     * @param msg 消息
     * @return 图片链接列表
     */
    public static List<String> getMsgImgUrlList(String msg) {
        List<String> imgUrlList = new ArrayList<>();
        for (String i : msg.split(ShiroUtilsEnum.CQ_CODE_SPLIT.getValue())) {
            if (i.startsWith("image")) {
                imgUrlList.add(RegexUtils.regex(ShiroUtilsEnum.GET_URL_REGEX.getValue(), msg));
            }
        }
        return imgUrlList;
    }

    /**
     * 获取消息内所有视频链接
     *
     * @param msg 消息
     * @return 视频链接列表
     */
    public static List<String> getMsgVideoUrlList(String msg) {
        List<String> videoUrlList = new ArrayList<>();
        for (String i : msg.split(ShiroUtilsEnum.CQ_CODE_SPLIT.getValue())) {
            if (i.startsWith("video")) {
                videoUrlList.add(RegexUtils.regex(ShiroUtilsEnum.GET_URL_REGEX.getValue(), msg));
            }
        }
        return videoUrlList;
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

}
