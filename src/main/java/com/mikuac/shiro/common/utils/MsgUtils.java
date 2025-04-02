package com.mikuac.shiro.common.utils;

import java.util.Base64;

/**
 * Created on 2021/7/8.
 *
 * @author Zero
 */
@SuppressWarnings("unused")
public class MsgUtils {

    private final StringBuilder builder = new StringBuilder();

    /**
     * 消息构建
     *
     * @return {@link MsgUtils}
     */
    public static MsgUtils builder() {
        return new MsgUtils();
    }

    /**
     * 文本内容
     *
     * @param text 内容
     * @return {@link MsgUtils}
     */
    public MsgUtils text(String text) {
        builder.append(text);
        return this;
    }

    /**
     * 图片
     * 支持本地图片、网络图片、Base64 详见 <a href="https://misakatat.github.io/shiro-docs/advanced.html#">消息构建工具</a>
     *
     * @param img 图片
     * @return {@link MsgUtils}
     */
    public MsgUtils img(String img) {
        String code = String.format("[CQ:image,file=%s]", ShiroUtils.escape(img));
        builder.append(code);
        return this;
    }

    /**
     * 图片
     *
     * @param media {@link OneBotMedia}
     * @return {@link MsgUtils}
     */
    public MsgUtils img(OneBotMedia media) {
        builder.append("[CQ:image,").append(media.escape()).append("]");
        return this;
    }

    /**
     * 图片
     *
     * @param image 图片字节数组
     * @return {@link MsgUtils}
     */
    public MsgUtils img(byte[] image) {
        String img = String.format("base64://%s", Base64.getEncoder().encodeToString(image));
        String code = String.format("[CQ:image,file=%s]", ShiroUtils.escape(img));
        builder.append(code);
        return this;
    }

    /**
     * 短视频
     *
     * @param video 视频地址, 支持 http 和 file 发送
     * @param cover 视频封面, 支持 http, file 和 base64 发送, 格式必须为 jpg
     * @return {@link MsgUtils}
     */
    public MsgUtils video(String video, String cover) {
        String code = String.format("[CQ:video,file=%s,cover=%s]", ShiroUtils.escape(video), ShiroUtils.escape(cover));
        builder.append(code);
        return this;
    }

    /**
     * 闪照
     *
     * @param img 图片
     * @return {@link MsgUtils}
     */
    public MsgUtils flashImg(String img) {
        String code = String.format("[CQ:image,type=flash,file=%s]", ShiroUtils.escape(img));
        builder.append(code);
        return this;
    }

    /**
     * QQ 表情
     * <a href="https://github.com/kyubotics/coolq-http-api/wiki/%E8%A1%A8%E6%83%85-CQ-%E7%A0%81-ID-%E8%A1%A8">对照表</a>
     *
     * @param id QQ 表情 ID
     * @return {@link MsgUtils}
     */
    public MsgUtils face(int id) {
        String code = String.format("[CQ:face,id=%s]", id);
        builder.append(code);
        return this;
    }

    /**
     * 语音
     *
     * @param media {@link OneBotMedia}
     * @return {@link MsgUtils}
     */
    public MsgUtils voice(OneBotMedia media) {
        builder.append("[CQ:record,").append(media.escape()).append("]");
        return this;
    }

    /**
     * 语音
     *
     * @param voice 语音, 支持本地文件和 URL
     * @return {@link MsgUtils}
     */
    public MsgUtils voice(String voice) {
        String code = String.format("[CQ:record,file=%s]", ShiroUtils.escape(voice));
        builder.append(code);
        return this;
    }

    /**
     * at 某人
     *
     * @param userId at 的 QQ 号, all 表示全体成员
     * @return {@link MsgUtils}
     */
    public MsgUtils at(long userId) {
        String code = String.format("[CQ:at,qq=%s]", userId);
        builder.append(code);
        return this;
    }

    /**
     * at 全体成员
     *
     * @return {@link MsgUtils}
     */
    public MsgUtils atAll() {
        builder.append("[CQ:at,qq=all]");
        return this;
    }

    /**
     * 戳一戳
     *
     * @param userId 需要戳的成员
     * @return {@link MsgUtils}
     */
    public MsgUtils poke(long userId) {
        String code = String.format("[CQ:poke,qq=%s]", userId);
        builder.append(code);
        return this;
    }

    /**
     * 回复
     *
     * @param msgId 回复时所引用的消息 id, 必须为本群消息.
     * @return {@link MsgUtils}
     */
    public MsgUtils reply(int msgId) {
        String code = String.format("[CQ:reply,id=%s]", msgId);
        builder.append(code);
        return this;
    }

    /**
     * 回复-频道
     *
     * @param msgId 回复时所引用的消息 id, 必须为本频道消息.
     * @return {@link MsgUtils}
     */
    public MsgUtils reply(String msgId) {
        String code = String.format("[CQ:reply,id=\"%s\"]", msgId);
        builder.append(code);
        return this;
    }

    /**
     * 礼物
     * 仅支持免费礼物, 发送群礼物消息 无法撤回, 返回的 message id 恒定为 0
     *
     * @param userId 接收礼物的成员
     * @param giftId 礼物的类型
     * @return {@link MsgUtils}
     */
    public MsgUtils gift(long userId, int giftId) {
        String code = String.format("[CQ:gift,qq=%s,id=%s]", userId, giftId);
        builder.append(code);
        return this;
    }

    /**
     * 文本转语音
     * 通过腾讯的 TTS 接口, 采用的音源与登录账号的性别有关
     *
     * @param text 内容
     * @return {@link MsgUtils}
     */
    public MsgUtils tts(String text) {
        String code = String.format("[CQ:tts,text=%s]", ShiroUtils.escape(text));
        builder.append(code);
        return this;
    }

    /**
     * XML 消息
     *
     * @param data xml内容, xml 中的 value 部分, 记得实体化处理
     * @return {@link MsgUtils}
     */
    public MsgUtils xml(String data) {
        String xmlCode = String.format("[CQ:xml,data=%s]", ShiroUtils.escape(data));
        builder.append(xmlCode);
        return this;
    }

    /**
     * XML 消息
     *
     * @param data  xml 内容, xml 中的 value部分, 记得实体化处理
     * @param resId 可以不填
     * @return {@link MsgUtils}
     */
    public MsgUtils xml(String data, int resId) {
        String code = String.format("[CQ:xml,data=%s,resid=%s]", ShiroUtils.escape(data), resId);
        builder.append(code);
        return this;
    }

    /**
     * JSON 消息
     *
     * @param data json 内容, json 的所有字符串记得实体化处理
     * @return {@link MsgUtils}
     */
    public MsgUtils json(String data) {
        String code = String.format("[CQ:json,data=%s]", ShiroUtils.escape(data));
        builder.append(code);
        return this;
    }

    /**
     * JSON 消息
     *
     * @param data  json 内容, json 的所有字符串记得实体化处理
     * @param resId 默认不填为 0, 走小程序通道, 填了走富文本通道发送
     * @return {@link MsgUtils}
     */
    public MsgUtils json(String data, int resId) {
        String code = String.format("[CQ:json,data=%s,resid=%s]", ShiroUtils.escape(data), resId);
        builder.append(code);
        return this;
    }

    /**
     * 一种 xml 的图片消息
     * xml 接口的消息都存在风控风险, 请自行兼容发送失败后的处理 ( 可以失败后走普通图片模式 )
     *
     * @param file 和 image 的 file 字段对齐, 支持也是一样的
     * @return {@link MsgUtils}
     */
    public MsgUtils cardImage(String file) {
        String code = String.format("[CQ:cardimage,file=%s]", ShiroUtils.escape(file));
        builder.append(code);
        return this;
    }

    /**
     * 一种 xml 的图片消息
     * xml 接口的消息都存在风控风险, 请自行兼容发送失败后的处理 ( 可以失败后走普通图片模式 )
     *
     * @param file      和 image 的 file 字段对齐, 支持也是一样的
     * @param minWidth  默认不填为 400, 最小 width
     * @param minHeight 默认不填为 400, 最小 height
     * @param maxWidth  默认不填为 500, 最大 width
     * @param maxHeight 默认不填为 1000, 最大 height
     * @param source    分享来源的名称, 可以留空
     * @param icon      分享来源的 icon 图标 url, 可以留空
     * @return {@link MsgUtils}
     */
    public MsgUtils cardImage(String file, long minWidth, long minHeight, long maxWidth, long maxHeight, String source, String icon) {
        String code =
                String.format("[CQ:cardimage,file=%s,minwidth=%s,minheight=%s,maxwidth=%s,maxheight=%s,source=%s,icon=%s]",
                        ShiroUtils.escape(file), minWidth, minHeight, maxWidth, maxHeight, ShiroUtils.escape(source),
                        ShiroUtils.escape(icon));
        builder.append(code);
        return this;
    }

    /**
     * 音乐分享
     *
     * @param type qq 163 xm (分别表示使用 QQ 音乐、网易云音乐、虾米音乐)
     * @param id   歌曲 ID
     * @return {@link MsgUtils}
     */
    public MsgUtils music(String type, long id) {
        String code = String.format("[CQ:music,type=%s,id=%s]", ShiroUtils.escape(type), id);
        builder.append(code);
        return this;
    }

    /**
     * 音乐自定义分享
     *
     * @param url     点击后跳转目标 URL
     * @param audio   音乐 URL
     * @param title   标题
     * @param content 发送时可选，内容描述
     * @param image   发送时可选，图片 URL
     * @return {@link MsgUtils}
     */
    public MsgUtils customMusic(String url, String audio, String title, String content, String image) {
        String code = String.format(
                "[CQ:music,type=custom,url=%s,audio=%s,title=%s,content=%s,image=%s]",
                ShiroUtils.escape(url), ShiroUtils.escape(audio), ShiroUtils.escape(title), ShiroUtils.escape(content),
                ShiroUtils.escape(image)
        );
        builder.append(code);
        return this;
    }

    /**
     * 音乐自定义分享
     *
     * @param url   点击后跳转目标 URL
     * @param audio 音乐 URL
     * @param title 标题
     * @return {@link MsgUtils}
     */
    public MsgUtils customMusic(String url, String audio, String title) {
        String code = String.format(
                "[CQ:music,type=custom,url=%s,audio=%s,title=%s]",
                ShiroUtils.escape(url), ShiroUtils.escape(audio), ShiroUtils.escape(title)
        );
        builder.append(code);
        return this;
    }

    /**
     * 发送猜拳消息
     *
     * @param value 0石头 1剪刀 2布
     * @return {@link MsgUtils}
     */
    public MsgUtils rps(int value) {
        String code = String.format("[CQ:rps,value=%s]", value);
        builder.append(code);
        return this;
    }

    /**
     * 发送长消息
     *
     * @param id 长消息Id
     * @return {@link MsgUtils}
     */
    public MsgUtils longMsg(String id) {
        String code = String.format("[CQ:longmsg,id=%s]", id);
        builder.append(code);
        return this;
    }

    /**
     * 发送合并转发消息
     *
     * @param id 合并转发消息Id
     * @return {@link MsgUtils}
     */
    public MsgUtils forward(String id) {
        String code = String.format("[CQ:forward,id=%s]", id);
        builder.append(code);
        return this;
    }

    /**
     * 构建消息链
     *
     * @return {@link String}
     */
    public String build() {
        return builder.toString();
    }

}
