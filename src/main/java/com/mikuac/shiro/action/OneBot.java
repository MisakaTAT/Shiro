package com.mikuac.shiro.action;

import com.mikuac.shiro.dto.action.common.*;
import com.mikuac.shiro.dto.action.response.*;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.model.ArrayMsg;

import java.util.List;

public interface OneBot {
    /**
     * 发送消息
     *
     * @param event      {@link AnyMessageEvent}
     * @param msg        要发送的内容
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    ActionData<MsgId> sendMsg(AnyMessageEvent event, String msg, boolean autoEscape);

    /**
     * 发送消息
     *
     * @param event      {@link AnyMessageEvent}
     * @param msg        消息链
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    ActionData<MsgId> sendMsg(AnyMessageEvent event, List<ArrayMsg> msg, boolean autoEscape);

    /**
     * 发送私聊消息
     *
     * @param userId     对方 QQ 号
     * @param msg        要发送的内容
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    ActionData<MsgId> sendPrivateMsg(long userId, String msg, boolean autoEscape);

    /**
     * 发送私聊消息
     *
     * @param userId     对方 QQ 号
     * @param msg        消息链
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    ActionData<MsgId> sendPrivateMsg(long userId, List<ArrayMsg> msg, boolean autoEscape);

    /**
     * 临时会话
     *
     * @param groupId    主动发起临时会话群号(机器人本身必须是管理员/群主)
     * @param userId     对方 QQ 号
     * @param msg        要发送的内容
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    ActionData<MsgId> sendPrivateMsg(long groupId, long userId, String msg, boolean autoEscape);

    /**
     * 临时会话
     *
     * @param groupId    主动发起临时会话群号(机器人本身必须是管理员/群主)
     * @param userId     对方 QQ 号
     * @param msg        消息链
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    ActionData<MsgId> sendPrivateMsg(long groupId, long userId, List<ArrayMsg> msg, boolean autoEscape);

    /**
     * 发送群消息
     *
     * @param groupId    群号
     * @param msg        要发送的内容
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    ActionData<MsgId> sendGroupMsg(long groupId, String msg, boolean autoEscape);

    /**
     * 发送群消息
     *
     * @param groupId    群号
     * @param msg        消息链
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    ActionData<MsgId> sendGroupMsg(long groupId, List<ArrayMsg> msg, boolean autoEscape);

    /**
     * 发送群消息
     *
     * @param groupId    群号
     * @param userId     调用者的QQ号 , 在QQ开放平台中用于设定@对象，如果不设置此参数会导致: 在bot返回前如果被不同用户多次调用，只会@最后一次调用的用户
     * @param msg        要发送的内容
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    ActionData<MsgId> sendGroupMsg(long groupId, long userId, String msg, boolean autoEscape);

    /**
     * 获取消息
     *
     * @param msgId 消息 ID
     * @return result {@link ActionData} of {@link MsgResp}
     */
    ActionData<MsgResp> getMsg(int msgId);

    /**
     * 撤回消息
     *
     * @param msgId 消息 ID
     * @return result {@link ActionRaw}
     */
    ActionRaw deleteMsg(int msgId);

    /**
     * 群组踢人
     *
     * @param groupId          群号
     * @param userId           要踢的 QQ 号
     * @param rejectAddRequest 拒绝此人的加群请求 (默认false)
     * @return result {@link ActionRaw}
     */
    ActionRaw setGroupKick(long groupId, long userId, boolean rejectAddRequest);

    /**
     * 群组单人禁言
     *
     * @param groupId  群号
     * @param userId   要禁言的 QQ 号
     * @param duration 禁言时长, 单位秒, 0 表示取消禁言 (默认30 * 60)
     * @return result {@link ActionRaw}
     */
    ActionRaw setGroupBan(long groupId, long userId, int duration);

    /**
     * 群组设置管理员
     *
     * @param groupId 群号
     * @param userId  要设置管理员的 QQ 号
     * @param enable  true 为设置，false 为取消
     * @return result {@link ActionRaw}
     */
    ActionRaw setGroupAdmin(long groupId, long userId, boolean enable);

    /**
     * 群组匿名
     *
     * @param groupId 群号
     * @param enable  是否允许匿名聊天
     * @return result {@link ActionRaw}
     */
    ActionRaw setGroupAnonymous(long groupId, boolean enable);

    /**
     * 设置群名片（群备注）
     *
     * @param groupId 群号
     * @param userId  要设置的 QQ 号
     * @param card    群名片内容，不填或空字符串表示删除群名片
     * @return result {@link ActionRaw}
     */
    ActionRaw setGroupCard(long groupId, long userId, String card);

    /**
     * 设置群名
     *
     * @param groupId   群号
     * @param groupName 新群名
     * @return result {@link ActionRaw}
     */
    ActionRaw setGroupName(long groupId, String groupName);

    /**
     * 退出群组
     *
     * @param groupId   群号
     * @param isDismiss 是否解散, 如果登录号是群主, 则仅在此项为 true 时能够解散
     * @return result {@link ActionRaw}
     */
    ActionRaw setGroupLeave(long groupId, boolean isDismiss);

    /**
     * 处理加好友请求
     *
     * @param flag    加好友请求的 flag（需从上报的数据中获得）
     * @param approve 是否同意请求(默认为true)
     * @param remark  添加后的好友备注（仅在同意时有效）
     * @return result {@link ActionRaw}
     */
    ActionRaw setFriendAddRequest(String flag, boolean approve, String remark);

    /**
     * 处理加群请求／邀请
     *
     * @param flag    加群请求的 flag（需从上报的数据中获得）
     * @param subType add 或 invite，请求类型（需要和上报消息中的 sub_type 字段相符）
     * @param approve 是否同意请求／邀请
     * @param reason  拒绝理由（仅在拒绝时有效）
     * @return result {@link ActionRaw}
     */
    ActionRaw setGroupAddRequest(String flag, String subType, boolean approve, String reason);

    /**
     * 获取陌生人信息
     *
     * @param userId  QQ 号
     * @param noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     * @return result {@link ActionData} of {@link StrangerInfoResp}
     */
    ActionData<StrangerInfoResp> getStrangerInfo(long userId, boolean noCache);

    /**
     * 获取好友列表
     *
     * @return result {@link ActionList} of {@link FriendInfoResp}
     */
    ActionList<FriendInfoResp> getFriendList();

    /**
     * 获取群信息
     *
     * @param groupId 群号
     * @param noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     * @return result {@link ActionData} of {@link GroupInfoResp}
     */
    ActionData<GroupInfoResp> getGroupInfo(long groupId, boolean noCache);

    /**
     * 获取群列表
     *
     * @return result {@link ActionList} of {@link GroupInfoResp}
     */
    ActionList<GroupInfoResp> getGroupList();

    /**
     * 获取群成员信息
     *
     * @param groupId 群号
     * @param userId  QQ 号
     * @param noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     * @return result {@link ActionData} of {@link GroupMemberInfoResp}
     */
    ActionData<GroupMemberInfoResp> getGroupMemberInfo(long groupId, long userId, boolean noCache);

    /**
     * 获取群成员列表
     *
     * @param groupId 群号
     * @return result {@link ActionList} of {@link GroupMemberInfoResp}
     */
    ActionList<GroupMemberInfoResp> getGroupMemberList(long groupId);

    /**
     * 获取群荣誉信息
     *
     * @param groupId 群号
     * @param type    要获取的群荣誉类型, 可传入 talkative performer legend strong_newbie emotion 以分别获取单个类型的群荣誉数据, 或传入 all 获取所有数据
     * @return result {@link ActionData} of {@link GroupHonorInfoResp}
     */
    ActionData<GroupHonorInfoResp> getGroupHonorInfo(long groupId, String type);

    /**
     * 检查是否可以发送图片
     *
     * @return result {@link ActionData} of {@link BooleanResp}
     */
    ActionData<BooleanResp> canSendImage();

    /**
     * 检查是否可以发送语音
     *
     * @return result {@link ActionData} of {@link BooleanResp}
     */
    ActionData<BooleanResp> canSendRecord();

    /**
     * 好友点赞
     *
     * @param userId 目标用户
     * @param times  点赞次数（每个好友每天最多 10 次，机器人为 Super VIP 则提高到 20次）
     * @return result {@link ActionRaw}
     */
    ActionRaw sendLike(long userId, int times);

    /**
     * 获取状态
     *
     * @return result {@link GetStatusResp}
     */
    ActionData<GetStatusResp> getStatus();

    /**
     * 群组匿名用户禁言
     *
     * @param groupId   群号
     * @param anonymous 要禁言的匿名用户对象（群消息上报的 anonymous 字段）
     * @param duration  禁言时长，单位秒，无法取消匿名用户禁言
     * @return result {@link ActionRaw}
     */
    ActionRaw setGroupAnonymousBan(long groupId, Anonymous anonymous, int duration);

    /**
     * 群组匿名用户禁言
     *
     * @param groupId  群号
     * @param flag     要禁言的匿名用户的 flag（需从群消息上报的数据中获得）
     * @param duration 禁言时长，单位秒，无法取消匿名用户禁言
     * @return result {@link ActionRaw}
     */
    ActionRaw setGroupAnonymousBan(long groupId, String flag, int duration);

    /**
     * 全体禁言
     *
     * @param groupId 群号
     * @param enable  是否禁言（默认True,False为取消禁言）
     * @return result {@link ActionRaw}
     */
    ActionRaw setGroupWholeBan(long groupId, boolean enable);

    /**
     * 获取登录号信息
     *
     * @return result {@link ActionData} of @{@link LoginInfoResp}
     */
    ActionData<LoginInfoResp> getLoginInfo();

}
