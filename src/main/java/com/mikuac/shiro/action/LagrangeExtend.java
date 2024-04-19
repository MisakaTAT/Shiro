package com.mikuac.shiro.action;

import com.mikuac.shiro.common.utils.Keyboard;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.ActionList;
import com.mikuac.shiro.dto.action.common.MsgId;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.model.ArrayMsg;

import java.util.List;

public interface LagrangeExtend {

    /**
     * 获取收藏表情
     *
     * @return 表情的下载 URL
     */
    ActionList<String> fetchCustomFace();

    /**
     * 发送消息
     *
     * @param event      {@link AnyMessageEvent}
     * @param msg        消息链
     * @param keyboard   使用{@link Keyboard}构造 不需要按钮就填 null
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    ActionData<MsgId> sendMarkdownMsg(AnyMessageEvent event, List<ArrayMsg> msg, Keyboard keyboard, boolean autoEscape);

    /**
     * 发送私聊消息
     *
     * @param userId     对方 QQ 号
     * @param msg        消息链
     * @param keyboard   使用{@link Keyboard}构造 不需要按钮就填 null
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    ActionData<MsgId> sendPrivateMarkdownMsg(long userId, List<ArrayMsg> msg, Keyboard keyboard, boolean autoEscape);

    /**
     * 发送私聊消息
     *
     * @param groupId     群号
     * @param msg        消息链
     * @param keyboard   使用{@link Keyboard}构造 不需要按钮就填 null
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    ActionData<MsgId> sendGroupMarkdownMsg(long groupId, List<ArrayMsg> msg, Keyboard keyboard, boolean autoEscape);

}
