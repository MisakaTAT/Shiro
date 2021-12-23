package com.mikuac.shiro.common.utils;

/**
 * Created on 2021/7/8.
 *
 * @author Zero
 */
public class MsgUtils {

    StringBuffer stringBuffer = new StringBuffer();

    /**
     * 消息构建对象
     *
     * @return Msg类
     */
    public static MsgUtils builder() {
        return new MsgUtils();
    }

    /**
     * 文本内容
     *
     * @param text 内容
     * @return CQ码
     */
    public MsgUtils text(String text) {
        stringBuffer.append(text);
        return this;
    }

    /**
     * 图片
     *
     * @param url 图片 URL
     * @return CQ码
     */
    public MsgUtils img(String url) {
        String imgCode = String.format("[CQ:image,file=%s]", ShiroUtils.escape(url));
        stringBuffer.append(imgCode);
        return this;
    }

    /**
     * 短视频
     *
     * @param video 视频地址, 支持http和file发送
     * @param cover 视频封面, 支持http, file和base64发送, 格式必须为jpg
     * @return CQ码
     */
    public MsgUtils video(String video, String cover) {
        String videoCode = String.format("[CQ:video,file=%s,cover=%s]", ShiroUtils.escape(video), ShiroUtils.escape(cover));
        stringBuffer.append(videoCode);
        return this;
    }

    /**
     * 闪照
     *
     * @param img 图片
     * @return CQ码
     */
    public MsgUtils flashImg(String img) {
        String flashImgCode = String.format("[CQ:image,type=flash,file=%s]", ShiroUtils.escape(img));
        stringBuffer.append(flashImgCode);
        return this;
    }

    /**
     * QQ 表情
     * QQ表情ID表
     * https://github.com/kyubotics/coolq-http-api/wiki/%E8%A1%A8%E6%83%85-CQ-%E7%A0%81-ID-%E8%A1%A8
     *
     * @param id QQ 表情 ID
     * @return CQ码
     */
    public MsgUtils face(int id) {
        String faceCode = String.format("[CQ:face,id=%s]", id);
        stringBuffer.append(faceCode);
        return this;
    }

    /**
     * 语音
     *
     * @param record 语音文件名
     * @return CQ码
     */
    public MsgUtils record(String record) {
        String recordCode = String.format("[CQ:record,file=%s]", ShiroUtils.escape(record));
        stringBuffer.append(recordCode);
        return this;
    }

    /**
     * at某人
     *
     * @param userId at的QQ号, all 表示全体成员
     * @return CQ码
     */
    public MsgUtils at(long userId) {
        String atCode = String.format("[CQ:at,qq=%s]", userId);
        stringBuffer.append(atCode);
        return this;
    }

    /**
     * at全体成员
     *
     * @return CQ码
     */
    public MsgUtils atAll() {
        stringBuffer.append("[CQ:at,qq=all]");
        return this;
    }

    /**
     * 戳一戳
     *
     * @param userId 需要戳的成员
     * @return CQ码
     */
    public MsgUtils poke(long userId) {
        String pokeCode = String.format("[CQ:poke,qq=%s]", userId);
        stringBuffer.append(pokeCode);
        return this;
    }

    /**
     * 回复
     *
     * @param msgId 回复时所引用的消息id, 必须为本群消息.
     * @return CQ码
     */
    public MsgUtils reply(int msgId) {
        String replyCode = String.format("[CQ:reply,id=%s]", msgId);
        stringBuffer.append(replyCode);
        return this;
    }

    /**
     * 礼物
     * 仅支持免费礼物, 发送群礼物消息 无法撤回, 返回的 message id 恒定为 0
     *
     * @param userId 接收礼物的成员
     * @param giftId 礼物的类型
     * @return CQ码
     */
    public MsgUtils gift(long userId, int giftId) {
        String giftCode = String.format("[CQ:gift,qq=%s,id=%s]", userId, giftId);
        stringBuffer.append(giftCode);
        return this;
    }

    /**
     * 文本转语音
     * 通过TX的TTS接口, 采用的音源与登录账号的性别有关
     *
     * @param text 内容
     * @return CQ码
     */
    public MsgUtils tts(String text) {
        String ttsCode = String.format("[CQ:tts,text=%s]", text);
        stringBuffer.append(ttsCode);
        return this;
    }

    /**
     * XML 消息
     *
     * @param data xml内容, xml中的value部分, 记得实体化处理
     * @return CQ码
     */
    public MsgUtils xml(String data) {
        String ttsCode = String.format("[CQ:xml,data=%s]", data);
        stringBuffer.append(ttsCode);
        return this;
    }

    /**
     * XML 消息
     *
     * @param data  xml内容, xml中的value部分, 记得实体化处理
     * @param resId 可以不填
     * @return CQ码
     */
    public MsgUtils xml(String data, int resId) {
        String ttsCode = String.format("[CQ:xml,data=%s,resid=%s]", data, resId);
        stringBuffer.append(ttsCode);
        return this;
    }

    /**
     * JSON 消息
     *
     * @param data json内容, json的所有字符串记得实体化处理
     * @return CQ码
     */
    public MsgUtils json(String data) {
        String ttsCode = String.format("[CQ:json,data=%s]", ShiroUtils.escape(data));
        stringBuffer.append(ttsCode);
        return this;
    }

    /**
     * JSON 消息
     *
     * @param data  json内容, json的所有字符串记得实体化处理
     * @param resId 默认不填为0, 走小程序通道, 填了走富文本通道发送
     * @return CQ码
     */
    public MsgUtils json(String data, int resId) {
        String ttsCode = String.format("[CQ:json,data=%s,resid=%s]", ShiroUtils.escape(data), resId);
        stringBuffer.append(ttsCode);
        return this;
    }

    /**
     * 一种xml的图片消息
     * xml 接口的消息都存在风控风险, 请自行兼容发送失败后的处理 ( 可以失败后走普通图片模式 )
     *
     * @param file 和image的file字段对齐, 支持也是一样的
     * @return CQ码
     */
    public MsgUtils cardImage(String file) {
        String ttsCode = String.format("[CQ:cardimage,file=%s]", ShiroUtils.escape(file));
        stringBuffer.append(ttsCode);
        return this;
    }

    /**
     * 一种xml的图片消息
     * xml 接口的消息都存在风控风险, 请自行兼容发送失败后的处理 ( 可以失败后走普通图片模式 )
     *
     * @param file      和image的file字段对齐, 支持也是一样的
     * @param minWidth  默认不填为400, 最小width
     * @param minHeight 默认不填为400, 最小height
     * @param maxWidth  默认不填为500, 最大width
     * @param maxHeight 默认不填为1000, 最大height
     * @param source    分享来源的名称, 可以留空
     * @param icon      分享来源的icon图标url, 可以留空
     * @return CQ码
     */
    public MsgUtils cardImage(String file, long minWidth, long minHeight, long maxWidth, long maxHeight, String source, String icon) {
        String ttsCode = String.format("[CQ:cardimage,file=%s,minwidth=%s,minheight=%s,maxwidth=%s,maxheight=%s,source=%s,icon=%s]",
                ShiroUtils.escape(file), minWidth, minHeight, maxWidth, maxHeight, source, ShiroUtils.escape(icon));
        stringBuffer.append(ttsCode);
        return this;
    }

    /**
     * 音乐分享
     *
     * @param type qq 163 xm (分别表示使用 QQ 音乐、网易云音乐、虾米音乐)
     * @param id   歌曲 ID
     * @return CQ码
     */
    public MsgUtils music(String type, String id) {
        String musicCode = String.format("[CQ:music,type=%s,id=%s]", type, id);
        stringBuffer.append(musicCode);
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
     * @return CQ码
     */
    public MsgUtils customMusic(String url, String audio, String title, String content, String image) {
        String customMusicCode = String.format(
                "[CQ:music,type=custom,url=%s,audio=%s,title=%s,content=%s,image=%s]",
                ShiroUtils.escape(url), ShiroUtils.escape(audio), title, content, ShiroUtils.escape(image)
        );
        stringBuffer.append(customMusicCode);
        return this;
    }

    /**
     * 音乐自定义分享
     *
     * @param url   点击后跳转目标 URL
     * @param audio 音乐 URL
     * @param title 标题
     * @return CQ码
     */
    public MsgUtils customMusic(String url, String audio, String title) {
        String customMusicCode = String.format(
                "[CQ:music,type=custom,url=%s,audio=%s,title=%s]",
                ShiroUtils.escape(url), ShiroUtils.escape(audio), title
        );
        stringBuffer.append(customMusicCode);
        return this;
    }

    /**
     * 构建消息链
     *
     * @return 字符串
     */
    public String build() {
        return stringBuffer.toString();
    }

}
