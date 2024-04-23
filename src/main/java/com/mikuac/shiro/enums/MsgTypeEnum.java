package com.mikuac.shiro.enums;

/**
 * @author zero
 */
@SuppressWarnings("squid:S115")
public enum MsgTypeEnum {

    /**
     * at 某人
     */
    at,

    /**
     * 文本类型
     */
    text,

    /**
     * 表情
     */
    face,

    /**
     * 商城大表情消息
     **/
    mface,

    /**
     * 投篮表情
     */
    basketball,

    /**
     * 语音
     */
    record,

    /**
     * 短视频
     */
    video,

    /**
     * 猜拳魔法表情
     */
    rps,

    /**
     * 新猜拳表情
     */
    new_rps,

    /**
     * 掷骰子魔法表情
     */
    dice,

    /**
     * 新掷骰子表情
     */
    new_dice,

    /**
     * 窗口抖动（戳一戳）
     */
    shake,

    /**
     * 匿名发消息
     */
    anonymous,

    /**
     * 链接分享
     */
    share,

    /**
     * 推荐好友/群
     */
    contact,

    /**
     * 位置
     */
    location,

    /**
     * 音乐分享
     */
    music,

    /**
     * 图片
     */
    image,

    /**
     * 回复
     */
    reply,

    /**
     * 红包
     */
    redbag,

    /**
     * 戳一戳
     */
    poke,

    /**
     * 礼物
     */
    gift,

    /**
     * 合并转发
     */
    forward,

    /**
     * 富文本消息
     */
    markdown,

    /**
     * 富文本下的按钮
     */
    keyboard,

    /**
     * 合并转发消息节点
     */
    node,

    /**
     * XML 消息
     */
    xml,

    /**
     * JSON 消息
     */
    json,

    /**
     * 一种 XML 的图片消息
     */
    cardimage,

    /**
     * 文本转语音
     */
    tts,

    /**
     * 长消息
     */
    longmsg,

    /**
     * 未知类型
     */
    unknown;

    public static MsgTypeEnum typeOf(String type) {
        for (MsgTypeEnum t : values()) {
            if (t.name().equals(type)) {
                return t;
            }
        }
        return unknown;
    }

}
