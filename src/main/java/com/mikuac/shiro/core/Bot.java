package com.mikuac.shiro.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.mikuac.shiro.dto.action.common.*;
import com.mikuac.shiro.dto.action.response.*;
import com.mikuac.shiro.enums.ActionPath;
import com.mikuac.shiro.enums.ActionPathEnum;
import com.mikuac.shiro.enums.MethodEnum;
import com.mikuac.shiro.handler.ActionHandler;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketSession;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created on 2021/7/7.
 *
 * @author Zero
 */
public class Bot {

    private final ActionHandler actionHandler;

    @Getter
    @Setter
    private long selfId;

    @Getter
    @Setter
    private WebSocketSession session;

    @Getter
    @Setter
    private List<Class<? extends BotPlugin>> pluginList;
    @Getter
    @Setter

    private MultiValueMap<Class<? extends Annotation>, Method> methods;

    /**
     * 构造函数
     *
     * @param selfId        机器人QQ账号
     * @param session       websocket session
     * @param actionHandler shiroActionHandler
     * @param pluginList    插件列表
     */
    public Bot(long selfId, WebSocketSession session, ActionHandler actionHandler, List<Class<? extends BotPlugin>> pluginList,
               MultiValueMap<Class<? extends Annotation>,Method> methods) {
        this.selfId = selfId;
        this.session = session;
        this.actionHandler = actionHandler;
        this.pluginList = pluginList;
        this.methods=methods;
    }

    /**
     * 发送私聊消息
     *
     * @param userId     对方 QQ 号
     * @param msg        要发送的内容
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return {@link ActionData<MsgId>}
     */
    public ActionData<MsgId> sendPrivateMsg(long userId, String msg, boolean autoEscape) {
        ActionPathEnum action = ActionPathEnum.SEND_PRIVATE_MSG;
        JSONObject params = new JSONObject();
        params.put("user_id", userId);
        params.put("message", msg);
        params.put("auto_escape", autoEscape);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(new TypeReference<ActionData<MsgId>>() {
        }) : null;
    }

    /**
     * 发送群消息
     *
     * @param groupId    群号
     * @param msg        要发送的内容
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return {@link ActionData<MsgId>}
     */
    public ActionData<MsgId> sendGroupMsg(long groupId, String msg, boolean autoEscape) {
        ActionPathEnum action = ActionPathEnum.SEND_GROUP_MSG;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("message", msg);
        params.put("auto_escape", autoEscape);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(new TypeReference<ActionData<MsgId>>() {
        }) : null;
    }

    /**
     * 获取消息
     *
     * @param msgId 消息 ID
     * @return {@link ActionData<GetMsgResp>}
     */
    public ActionData<GetMsgResp> getMsg(int msgId) {
        ActionPathEnum action = ActionPathEnum.GET_MSG;
        JSONObject params = new JSONObject();
        params.put("message_id", msgId);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(new TypeReference<ActionData<GetMsgResp>>() {
        }) : null;
    }

    /**
     * 撤回消息
     *
     * @param msgId 消息 ID
     * @return {@link ActionRaw}
     */
    public ActionRaw deleteMsg(int msgId) {
        ActionPathEnum action = ActionPathEnum.DELETE_MSG;
        JSONObject params = new JSONObject();
        params.put("message_id", msgId);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 群组踢人
     *
     * @param groupId          群号
     * @param userId           要踢的 QQ 号
     * @param rejectAddRequest 拒绝此人的加群请求 (默认false)
     * @return {@link ActionRaw}
     */
    public ActionRaw setGroupKick(long groupId, long userId, boolean rejectAddRequest) {
        ActionPathEnum action = ActionPathEnum.SET_GROUP_KICK;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("reject_add_request", rejectAddRequest);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 群组单人禁言
     *
     * @param groupId  群号
     * @param userId   要禁言的 QQ 号
     * @param duration 禁言时长, 单位秒, 0 表示取消禁言 (默认30 * 60)
     * @return {@link ActionRaw}
     */
    public ActionRaw setGroupBan(long groupId, long userId, int duration) {
        ActionPathEnum action = ActionPathEnum.SET_GROUP_BAN;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("duration", duration);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 全体禁言
     *
     * @param groupId 群号
     * @param enable  是否禁言（默认True,False为取消禁言）
     * @return {@link ActionRaw}
     */
    public ActionRaw setGroupWholeBan(long groupId, boolean enable) {
        ActionPathEnum action = ActionPathEnum.SET_GROUP_WHOLE_BAN;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("enable", enable);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 群组设置管理员
     *
     * @param groupId 群号
     * @param userId  要设置管理员的 QQ 号
     * @param enable  true 为设置，false 为取消
     * @return {@link ActionRaw}
     */
    public ActionRaw setGroupAdmin(long groupId, long userId, boolean enable) {
        ActionPathEnum action = ActionPathEnum.SET_GROUP_ADMIN;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("enable", enable);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 群组匿名
     *
     * @param groupId 群号
     * @param enable  是否允许匿名聊天
     * @return {@link ActionRaw}
     */
    public ActionRaw setGroupAnonymous(long groupId, boolean enable) {
        ActionPathEnum action = ActionPathEnum.SET_GROUP_ANONYMOUS;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("enable", enable);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 设置群名片（群备注）
     *
     * @param groupId 群号
     * @param userId  要设置的 QQ 号
     * @param card    群名片内容，不填或空字符串表示删除群名片
     * @return {@link ActionRaw}
     */
    public ActionRaw setGroupCard(long groupId, long userId, String card) {
        ActionPathEnum action = ActionPathEnum.SET_GROUP_CARD;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("card", card);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 设置群名
     *
     * @param groupId   群号
     * @param groupName 新群名
     * @return {@link ActionRaw}
     */
    public ActionRaw setGroupName(long groupId, String groupName) {
        ActionPathEnum action = ActionPathEnum.SET_GROUP_NAME;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("group_name", groupName);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 退出群组
     *
     * @param groupId   群号
     * @param isDismiss 是否解散, 如果登录号是群主, 则仅在此项为 true 时能够解散
     * @return {@link ActionRaw}
     */
    public ActionRaw setGroupLeave(long groupId, boolean isDismiss) {
        ActionPathEnum action = ActionPathEnum.SET_GROUP_LEAVE;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("is_dismiss", isDismiss);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 设置群组专属头衔
     *
     * @param groupId      群号
     * @param userId       要设置的 QQ 号
     * @param specialTitle 专属头衔，不填或空字符串表示删除专属头衔
     * @param duration     专属头衔有效期，单位秒，-1 表示永久，不过此项似乎没有效果，可能是只有某些特殊的时间长度有效，有待测试
     * @return {@link ActionRaw}
     */
    public ActionRaw setGroupSpecialTitle(long groupId, long userId, String specialTitle, int duration) {
        ActionPathEnum action = ActionPathEnum.SET_GROUP_SPECIAL_TITLE;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("special_title", specialTitle);
        params.put("duration", duration);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 处理加好友请求
     *
     * @param flag    加好友请求的 flag（需从上报的数据中获得）
     * @param approve 是否同意请求(默认为true)
     * @param remark  添加后的好友备注（仅在同意时有效）
     * @return {@link ActionRaw}
     */
    public ActionRaw setFriendAddRequest(String flag, boolean approve, String remark) {
        ActionPathEnum action = ActionPathEnum.SET_FRIEND_ADD_REQUEST;
        JSONObject params = new JSONObject();
        params.put("flag", flag);
        params.put("approve", approve);
        params.put("remark", remark);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 处理加群请求／邀请
     *
     * @param flag    加群请求的 flag（需从上报的数据中获得）
     * @param subType add 或 invite，请求类型（需要和上报消息中的 sub_type 字段相符）
     * @param approve 是否同意请求／邀请
     * @param reason  拒绝理由（仅在拒绝时有效）
     * @return {@link ActionRaw}
     */
    public ActionRaw setGroupAddRequest(String flag, String subType, boolean approve, String reason) {
        ActionPathEnum action = ActionPathEnum.SET_GROUP_ADD_REQUEST;
        JSONObject params = new JSONObject();
        params.put("flag", flag);
        params.put("sub_type", subType);
        params.put("approve", approve);
        params.put("reason", reason);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 获取登录号信息
     *
     * @return {@link ActionData<LoginInfoResp>}
     */
    public ActionData<LoginInfoResp> getLoginInfo() {
        ActionPathEnum action = ActionPathEnum.GET_LOGIN_INFO;
        JSONObject result = actionHandler.doActionRequest(session, action, null);
        return result != null ? result.toJavaObject(new TypeReference<ActionData<LoginInfoResp>>() {
        }) : null;
    }

    /**
     * 获取陌生人信息
     *
     * @param userId  QQ 号
     * @param noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     * @return {@link ActionData<StrangerInfoResp>}
     */
    public ActionData<StrangerInfoResp> getStrangerInfo(long userId, boolean noCache) {
        ActionPathEnum action = ActionPathEnum.GET_STRANGER_INFO;
        JSONObject params = new JSONObject();
        params.put("user_id", userId);
        params.put("no_cache", noCache);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(new TypeReference<ActionData<StrangerInfoResp>>() {
        }) : null;
    }

    /**
     * 获取好友列表
     *
     * @return {@link ActionList<FriendInfoResp>}
     */
    public ActionList<FriendInfoResp> getFriendList() {
        ActionPathEnum action = ActionPathEnum.GET_FRIEND_LIST;
        JSONObject result = actionHandler.doActionRequest(session, action, null);
        return result != null ? result.toJavaObject(new TypeReference<ActionList<FriendInfoResp>>() {
        }) : null;
    }

    /**
     * 删除好友
     *
     * @param friendId 好友 QQ 号
     * @return {@link ActionRaw}
     */
    public ActionRaw deleteFriend(long friendId) {
        ActionPathEnum action = ActionPathEnum.DELETE_FRIEND;
        JSONObject params = new JSONObject();
        params.put("friend_id", friendId);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 获取群信息
     *
     * @param groupId 群号
     * @param noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     * @return {@link ActionData<GroupInfoResp>}
     */
    public ActionData<GroupInfoResp> getGroupInfo(long groupId, boolean noCache) {
        ActionPathEnum action = ActionPathEnum.GET_GROUP_INFO;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("no_cache", noCache);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(new TypeReference<ActionData<GroupInfoResp>>() {
        }) : null;
    }

    /**
     * 获取群列表
     *
     * @return {@link ActionList<GroupInfoResp>}
     */
    public ActionList<GroupInfoResp> getGroupList() {
        ActionPathEnum action = ActionPathEnum.GET_GROUP_LIST;
        JSONObject result = actionHandler.doActionRequest(session, action, null);
        return result != null ? result.toJavaObject(new TypeReference<ActionList<GroupInfoResp>>() {
        }) : null;
    }

    /**
     * 获取群成员信息
     *
     * @param groupId 群号
     * @param userId  QQ 号
     * @param noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     * @return {@link ActionData<GroupMemberInfoResp>}
     */
    public ActionData<GroupMemberInfoResp> getGroupMemberInfo(long groupId, long userId, boolean noCache) {
        ActionPathEnum action = ActionPathEnum.GET_GROUP_MEMBER_INFO;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("user_id", userId);
        params.put("no_cache", noCache);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(new TypeReference<ActionData<GroupMemberInfoResp>>() {
        }) : null;
    }

    /**
     * 获取群成员列表
     *
     * @param groupId 群号
     * @return {@link ActionList<GroupMemberInfoResp>}
     */
    public ActionList<GroupMemberInfoResp> getGroupMemberList(long groupId) {
        ActionPathEnum action = ActionPathEnum.GET_GROUP_MEMBER_LIST;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(new TypeReference<ActionList<GroupMemberInfoResp>>() {
        }) : null;
    }

    /**
     * 获取群荣誉信息
     *
     * @param groupId 群号
     * @param type    要获取的群荣誉类型, 可传入 talkative performer legend strong_newbie emotion 以分别获取单个类型的群荣誉数据, 或传入 all 获取所有数据
     * @return {@link ActionData<GroupHonorInfoResp>}
     */
    public ActionData<GroupHonorInfoResp> getGroupHonorInfo(long groupId, String type) {
        ActionPathEnum action = ActionPathEnum.GET_GROUP_HONOR_INFO;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("type", type);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(new TypeReference<ActionData<GroupHonorInfoResp>>() {
        }) : null;
    }

    /**
     * 检查是否可以发送图片
     *
     * @return {@link ActionData<BooleanResp>}
     */
    public ActionData<BooleanResp> canSendImage() {
        ActionPathEnum action = ActionPathEnum.CAN_SEND_IMAGE;
        JSONObject result = actionHandler.doActionRequest(session, action, null);
        return result != null ? result.toJavaObject(new TypeReference<ActionData<BooleanResp>>() {
        }) : null;
    }

    /**
     * 检查是否可以发送语音
     *
     * @return {@link ActionData<BooleanResp>}
     */
    public ActionData<BooleanResp> canSendRecord() {
        ActionPathEnum action = ActionPathEnum.CAN_SEND_RECORD;
        JSONObject result = actionHandler.doActionRequest(session, action, null);
        return result != null ? result.toJavaObject(new TypeReference<ActionData<BooleanResp>>() {
        }) : null;
    }

    /**
     * 设置群头像
     * 目前这个API在登录一段时间后因cookie失效而失效, 请考虑后使用
     *
     * @param groupId 群号
     * @param file    图片文件名（支持绝对路径，网络URL，Base64编码）
     * @param cache   表示是否使用已缓存的文件 （通过网络URL发送时有效, 1表示使用缓存, 0关闭关闭缓存, 默认为1）
     * @return {@link ActionRaw}
     */
    public ActionRaw setGroupPortrait(long groupId, String file, int cache) {
        ActionPathEnum action = ActionPathEnum.SET_GROUP_PORTRAIT;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("file", file);
        params.put("cache", cache);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 检查链接安全性
     * 安全等级, 1: 安全 2: 未知 3: 危险
     *
     * @param url 需要检查的链接
     * @return {@link ActionData<CheckUrlSafelyResp>}
     */
    public ActionData<CheckUrlSafelyResp> checkUrlSafely(String url) {
        ActionPathEnum action = ActionPathEnum.CHECK_URL_SAFELY;
        JSONObject params = new JSONObject();
        params.put("url", url);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(new TypeReference<ActionData<CheckUrlSafelyResp>>() {
        }) : null;
    }

    /**
     * 发送群公告
     *
     * @param groupId 群号
     * @param content 公告内容
     * @return {@link ActionRaw}
     */
    public ActionRaw sendGroupNotice(long groupId, String content) {
        ActionPathEnum action = ActionPathEnum.SEN_GROUP_NOTICE;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("content", content);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 获取群 @全体成员 剩余次数
     *
     * @param groupId 群号
     * @return {@link ActionData<GroupAtAllRemainResp>}
     */
    public ActionData<GroupAtAllRemainResp> getGroupAtAllRemain(long groupId) {
        ActionPathEnum action = ActionPathEnum.GET_GROUP_AT_ALL_REMAIN;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(new TypeReference<ActionData<GroupAtAllRemainResp>>() {
        }) : null;
    }

    /**
     * 上传群文件
     * 在不提供 folder 参数的情况下默认上传到根目录
     * 只能上传本地文件, 需要上传 http 文件的话请先下载到本地
     *
     * @param groupId 群号
     * @param file    本地文件路径
     * @param name    储存名称
     * @param folder  父目录ID
     * @return {@link ActionRaw}
     */
    public ActionRaw uploadGroupFile(long groupId, String file, String name, String folder) {
        ActionPathEnum action = ActionPathEnum.UPLOAD_GROUP_FILE;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("file", file);
        params.put("name", name);
        params.put("folder", folder);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 上传群文件
     * 在不提供 folder 参数的情况下默认上传到根目录
     * 只能上传本地文件, 需要上传 http 文件的话请先下载到本地
     *
     * @param groupId 群号
     * @param file    本地文件路径
     * @param name    储存名称
     * @return {@link ActionRaw}
     */
    public ActionRaw uploadGroupFile(long groupId, String file, String name) {
        ActionPathEnum action = ActionPathEnum.UPLOAD_GROUP_FILE;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("file", file);
        params.put("name", name);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 群组匿名用户禁言
     *
     * @param groupId   群号
     * @param anonymous 要禁言的匿名用户对象（群消息上报的 anonymous 字段）
     * @param duration  禁言时长，单位秒，无法取消匿名用户禁言
     * @return {@link ActionRaw}
     */
    public ActionRaw setGroupAnonymousBan(long groupId, Anonymous anonymous, boolean duration) {
        ActionPathEnum action = ActionPathEnum.SET_GROUP_ANONYMOUS_BAN;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("anonymous", anonymous);
        params.put("duration", duration);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 群组匿名用户禁言
     *
     * @param groupId  群号
     * @param flag     要禁言的匿名用户的 flag（需从群消息上报的数据中获得）
     * @param duration 禁言时长，单位秒，无法取消匿名用户禁言
     * @return {@link ActionRaw}
     */
    public ActionRaw setGroupAnonymousBan(long groupId, String flag, boolean duration) {
        ActionPathEnum action = ActionPathEnum.SET_GROUP_ANONYMOUS_BAN;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("flag", flag);
        params.put("duration", duration);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 调用 go cq http 下载文件
     *
     * @param url         链接地址
     * @param threadCount 下载线程数
     * @param headers     自定义请求头
     * @return {@link ActionData<DownloadFileResp>}
     */
    public ActionData<DownloadFileResp> downloadFile(String url, int threadCount, String headers) {
        ActionPathEnum action = ActionPathEnum.DOWNLOAD_FILE;
        JSONObject params = new JSONObject();
        params.put("url", url);
        params.put("thread_count", threadCount);
        params.put("headers", headers);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(new TypeReference<ActionData<DownloadFileResp>>() {
        }) : null;
    }

    /**
     * 调用 go cq http 下载文件
     *
     * @param url 链接地址
     * @return {@link ActionData<DownloadFileResp>}
     */
    public ActionData<DownloadFileResp> downloadFile(String url) {
        ActionPathEnum action = ActionPathEnum.DOWNLOAD_FILE;
        JSONObject params = new JSONObject();
        params.put("url", url);
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(new TypeReference<ActionData<DownloadFileResp>>() {
        }) : null;

    }

    /**
     * 发送合并转发 (群)
     *
     * @param groupId 群号
     * @param msg     自定义转发消息, 具体看 https://docs.go-cqhttp.org/cqcode/#%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91
     * @return {@link ActionRaw}
     */
    public ActionRaw sendGroupForwardMsg(long groupId, JSONArray msg) {
        ActionPathEnum action = ActionPathEnum.SEND_GROUP_FORWARD_MSG;
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        params.put("messages", msg.toString());
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionRaw.class) : null;
    }

    /**
     * 自定义请求
     *
     * @param action 请求路径
     * @param params 请求参数
     * @return {@link ActionData}
     */
    @SuppressWarnings("rawtypes")
    public ActionData customRequest(ActionPath action, JSONObject params) {
        JSONObject result = actionHandler.doActionRequest(session, action, params);
        return result != null ? result.toJavaObject(ActionData.class) : null;
    }

}
