package com.mikuac.shiro.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mikuac.shiro.action.*;
import com.mikuac.shiro.common.utils.ConnectionUtils;
import com.mikuac.shiro.common.utils.JsonObjectWrapper;
import com.mikuac.shiro.common.utils.JsonUtils;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.dto.action.common.*;
import com.mikuac.shiro.dto.action.response.*;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.enums.ActionPath;
import com.mikuac.shiro.enums.ActionPathEnum;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.model.ArrayMsg;
import com.mikuac.shiro.model.HandlerMethod;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.Closeable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created on 2021/7/7.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Getter
@Setter
@SuppressWarnings({"unused", "Duplicates"})
public class Bot implements OneBot, GoCQHTTPExtend, GensokyoExtend, LagrangeExtend, LLOneBotExtend, NapCatExtend, Closeable {

    private long selfId;

    private String token;

    private ActionHandler actionHandler;

    private WebSocketSession session;

    private List<Class<? extends BotPlugin>> pluginList;

    private BotFactory.AnnotationMethodContainer annotationMethodContainer;

    private Class<? extends BotMessageEventInterceptor> botMessageEventInterceptor;

    public Bot(long selfId, WebSocketSession session, ActionHandler actionHandler, List<Class<? extends BotPlugin>> pluginList, BotFactory.AnnotationMethodContainer annotationMethodContainer, Class<? extends BotMessageEventInterceptor> botMessageEventInterceptor) {
        this.selfId = selfId;
        this.session = session;
        this.actionHandler = actionHandler;
        this.pluginList = pluginList;
        this.annotationMethodContainer = annotationMethodContainer;
        this.botMessageEventInterceptor = botMessageEventInterceptor;
        token = ConnectionUtils.getAuthorization(session);
    }

    public MultiValueMap<Class<? extends Annotation>, HandlerMethod> getAnnotationHandler() {
        return annotationMethodContainer.getAnnotationHandler();
    }

    @Override
    public void close() throws IOException {
        if (session.isOpen()) {
            session.close(CloseStatus.NORMAL);
        }
    }

    /**
     * 获取客户端连接的 token
     *
     * @return token, 如果未提供, 则为 null
     */
    @Nullable
    public String getToken() {
        return token;
    }

    /**
     * 发送消息
     *
     * @param event      {@link AnyMessageEvent}
     * @param msg        要发送的内容
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    @Override
    public ActionData<MsgId> sendMsg(AnyMessageEvent event, String msg, boolean autoEscape) {
        if (ActionParams.PRIVATE.equals(event.getMessageType())) {
            return sendPrivateMsg(event.getUserId(), msg, autoEscape);
        }
        if (ActionParams.GROUP.equals(event.getMessageType())) {
            return sendGroupMsg(event.getGroupId(), msg, autoEscape);
        }
        return null;
    }

    /**
     * 发送消息
     *
     * @param event      {@link AnyMessageEvent}
     * @param msg        消息链
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    @Override
    public ActionData<MsgId> sendMsg(AnyMessageEvent event, List<ArrayMsg> msg, boolean autoEscape) {
        if (ActionParams.PRIVATE.equals(event.getMessageType())) {
            return sendPrivateMsg(event.getUserId(), msg, autoEscape);
        }
        if (ActionParams.GROUP.equals(event.getMessageType())) {
            return sendGroupMsg(event.getGroupId(), msg, autoEscape);
        }
        return null;
    }

    /**
     * 发送私聊消息
     *
     * @param userId     对方 QQ 号
     * @param msg        要发送的内容
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    @Override
    public ActionData<MsgId> sendPrivateMsg(long userId, String msg, boolean autoEscape) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.MESSAGE, msg);
        params.put(ActionParams.AUTO_ESCAPE, autoEscape);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_PRIVATE_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 发送私聊消息
     *
     * @param userId     对方 QQ 号
     * @param msg        消息链
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    @Override
    public ActionData<MsgId> sendPrivateMsg(long userId, List<ArrayMsg> msg, boolean autoEscape) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.MESSAGE, msg);
        params.put(ActionParams.AUTO_ESCAPE, autoEscape);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_PRIVATE_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 临时会话
     *
     * @param groupId    主动发起临时会话群号(机器人本身必须是管理员/群主)
     * @param userId     对方 QQ 号
     * @param msg        要发送的内容
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    @Override
    public ActionData<MsgId> sendPrivateMsg(long groupId, long userId, String msg, boolean autoEscape) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.MESSAGE, msg);
        params.put(ActionParams.AUTO_ESCAPE, autoEscape);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_PRIVATE_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 临时会话
     *
     * @param groupId    主动发起临时会话群号(机器人本身必须是管理员/群主)
     * @param userId     对方 QQ 号
     * @param msg        消息链
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    @Override
    public ActionData<MsgId> sendPrivateMsg(long groupId, long userId, List<ArrayMsg> msg, boolean autoEscape) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.MESSAGE, msg);
        params.put(ActionParams.AUTO_ESCAPE, autoEscape);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_PRIVATE_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 发送群消息
     *
     * @param groupId    群号
     * @param msg        要发送的内容
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    @Override
    public ActionData<MsgId> sendGroupMsg(long groupId, String msg, boolean autoEscape) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.MESSAGE, msg);
        params.put(ActionParams.AUTO_ESCAPE, autoEscape);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_GROUP_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 发送群消息
     *
     * @param groupId    群号
     * @param msg        消息链
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    @Override
    public ActionData<MsgId> sendGroupMsg(long groupId, List<ArrayMsg> msg, boolean autoEscape) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.MESSAGE, msg);
        params.put(ActionParams.AUTO_ESCAPE, autoEscape);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_GROUP_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 发送群消息
     *
     * @param groupId    群号
     * @param userId     调用者的QQ号 , 在QQ开放平台中用于设定@对象，如果不设置此参数会导致: 在bot返回前如果被不同用户多次调用，只会@最后一次调用的用户
     * @param msg        要发送的内容
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    @Override
    public ActionData<MsgId> sendGroupMsg(long groupId, long userId, String msg, boolean autoEscape) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.MESSAGE, msg);
        params.put(ActionParams.AUTO_ESCAPE, autoEscape);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_GROUP_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 发送群消息
     *
     * @param groupId    群号
     * @param userId     调用者的QQ号 , 在QQ开放平台中用于设定@对象，如果不设置此参数会导致: 在bot返回前如果被不同用户多次调用，只会@最后一次调用的用户
     * @param msg        要发送的内容
     * @param autoEscape 消息内容是否作为纯文本发送 ( 即不解析 CQ 码 ) , 只在 message 字段是字符串时有效
     * @return result {@link ActionData} of {@link MsgId}
     */
    @Override
    public ActionData<MsgId> sendGroupMsg(long groupId, long userId, List<ArrayMsg> msg, boolean autoEscape) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.MESSAGE, msg);
        params.put(ActionParams.AUTO_ESCAPE, autoEscape);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_GROUP_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取频道成员列表
     * 由于频道人数较多(数万), 请尽量不要全量拉取成员列表, 这将会导致严重的性能问题
     * 尽量使用 getGuildMemberProfile 接口代替全量拉取
     * nextToken 为空的情况下, 将返回第一页的数据, 并在返回值附带下一页的 token
     *
     * @param guildId   频道ID
     * @param nextToken 翻页Token
     * @return result {@link ActionData} of {@link GuildMemberListResp}
     */
    @Override
    public ActionData<GuildMemberListResp> getGuildMemberList(String guildId, String nextToken) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GUILD_ID, guildId);
        params.put(ActionParams.NEXT_TOKEN, nextToken);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GUILD_LIST, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 发送信息到子频道
     *
     * @param guildId   频道 ID
     * @param channelId 子频道 ID
     * @param msg       要发送的内容
     * @return result {@link ActionData} of {@link GuildMsgId}
     */
    @Override
    public ActionData<GuildMsgId> sendGuildMsg(String guildId, String channelId, String msg) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GUILD_ID, guildId);
        params.put(ActionParams.CHANNEL_ID, channelId);
        params.put(ActionParams.MESSAGE, msg);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_GUILD_CHANNEL_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取频道消息
     *
     * @param guildMsgId 频道 ID
     * @param noCache    是否使用缓存
     * @return result {@link ActionData} of {@link GetGuildMsgResp}
     */
    @Override
    public ActionData<GetGuildMsgResp> getGuildMsg(String guildMsgId, boolean noCache) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.MESSAGE_ID, guildMsgId);
        params.put(ActionParams.NO_CACHE, noCache);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GUILD_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取频道系统内 BOT 的资料
     *
     * @return result {@link ActionData} of {@link GuildServiceProfileResp}
     */
    @Override
    public ActionData<GuildServiceProfileResp> getGuildServiceProfile() {
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GUILD_SERVICE_PROFILE, null);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取频道列表
     *
     * @return result {@link ActionList} of {@link GuildListResp}
     */
    @Override
    public ActionList<GuildListResp> getGuildList() {
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GUILD_LIST, null);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 通过访客获取频道元数据
     *
     * @param guildId 频道 ID
     * @return result {@link ActionData} of {@link GuildMetaByGuestResp}
     */
    @Override
    public ActionData<GuildMetaByGuestResp> getGuildMetaByGuest(String guildId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GUILD_ID, guildId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GUILD_META_BY_GUEST, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取子频道列表
     *
     * @param guildId 频道 ID
     * @param noCache 是否无视缓存
     * @return result {@link ActionList} of {@link ChannelInfoResp}
     */
    @Override
    public ActionList<ChannelInfoResp> getGuildChannelList(String guildId, boolean noCache) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GUILD_ID, guildId);
        params.put(ActionParams.NO_CACHE, noCache);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GUILD_CHANNEL_LIST, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 单独获取频道成员信息
     *
     * @param guildId 频道ID
     * @param userId  用户ID
     * @return result {@link ActionData} of {@link GuildMemberProfileResp}
     */
    @Override
    public ActionData<GuildMemberProfileResp> getGuildMemberProfile(String guildId, String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GUILD_ID, guildId);
        params.put(ActionParams.USER_ID, userId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GUILD_MEMBER_PROFILE, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取消息
     *
     * @param msgId 消息 ID
     * @return result {@link ActionData} of {@link MsgResp}
     */
    @Override
    public ActionData<MsgResp> getMsg(int msgId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.MESSAGE_ID, msgId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 撤回消息
     *
     * @param msgId 消息 ID
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw deleteMsg(int msgId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.MESSAGE_ID, msgId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.DELETE_MSG, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 撤回消息（兼容gsk）
     *
     * @param groupId 群号
     * @param userId  用户id
     * @param msgId   消息 ID
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw deleteMsg(long groupId, long userId, int msgId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.MESSAGE_ID, msgId);
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.USER_ID, userId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.DELETE_MSG, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 群组踢人
     *
     * @param groupId          群号
     * @param userId           要踢的 QQ 号
     * @param rejectAddRequest 拒绝此人的加群请求 (默认false)
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setGroupKick(long groupId, long userId, boolean rejectAddRequest) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.REJECT_ADD_REQUEST, rejectAddRequest);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_GROUP_KICK, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 群组单人禁言
     *
     * @param groupId  群号
     * @param userId   要禁言的 QQ 号
     * @param duration 禁言时长, 单位秒, 0 表示取消禁言 (默认30 * 60)
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setGroupBan(long groupId, long userId, int duration) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.DURATION, duration);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_GROUP_BAN, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 全体禁言
     *
     * @param groupId 群号
     * @param enable  是否禁言（默认True,False为取消禁言）
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setGroupWholeBan(long groupId, boolean enable) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.ENABLE, enable);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_GROUP_WHOLE_BAN, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 群组设置管理员
     *
     * @param groupId 群号
     * @param userId  要设置管理员的 QQ 号
     * @param enable  true 为设置，false 为取消
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setGroupAdmin(long groupId, long userId, boolean enable) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.ENABLE, enable);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_GROUP_ADMIN, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 群组匿名
     *
     * @param groupId 群号
     * @param enable  是否允许匿名聊天
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setGroupAnonymous(long groupId, boolean enable) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.ENABLE, enable);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_GROUP_ANONYMOUS, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 设置群名片（群备注）
     *
     * @param groupId 群号
     * @param userId  要设置的 QQ 号
     * @param card    群名片内容，不填或空字符串表示删除群名片
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setGroupCard(long groupId, long userId, String card) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.CARD, card);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_GROUP_CARD, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 设置群名
     *
     * @param groupId   群号
     * @param groupName 新群名
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setGroupName(long groupId, String groupName) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.GROUP_NAME, groupName);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_GROUP_NAME, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 退出群组
     *
     * @param groupId   群号
     * @param isDismiss 是否解散, 如果登录号是群主, 则仅在此项为 true 时能够解散
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setGroupLeave(long groupId, boolean isDismiss) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.IS_DISMISS, isDismiss);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_GROUP_LEAVE, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 设置群组专属头衔
     *
     * @param groupId      群号
     * @param userId       要设置的 QQ 号
     * @param specialTitle 专属头衔，不填或空字符串表示删除专属头衔
     * @param duration     专属头衔有效期，单位秒，-1 表示永久，不过此项似乎没有效果，可能是只有某些特殊的时间长度有效，有待测试
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setGroupSpecialTitle(long groupId, long userId, String specialTitle, int duration) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.SPECIAL_TITLE, specialTitle);
        params.put(ActionParams.DURATION, duration);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_GROUP_SPECIAL_TITLE, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 处理加好友请求
     *
     * @param flag    加好友请求的 flag（需从上报的数据中获得）
     * @param approve 是否同意请求(默认为true)
     * @param remark  添加后的好友备注（仅在同意时有效）
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setFriendAddRequest(String flag, boolean approve, String remark) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.FLAG, flag);
        params.put(ActionParams.APPROVE, approve);
        params.put(ActionParams.REMARK, remark);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_FRIEND_ADD_REQUEST, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 处理加群请求／邀请
     *
     * @param flag    加群请求的 flag（需从上报的数据中获得）
     * @param subType add 或 invite，请求类型（需要和上报消息中的 sub_type 字段相符）
     * @param approve 是否同意请求／邀请
     * @param reason  拒绝理由（仅在拒绝时有效）
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setGroupAddRequest(String flag, String subType, boolean approve, String reason) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.FLAG, flag);
        params.put(ActionParams.SUB_TYPE, subType);
        params.put(ActionParams.APPROVE, approve);
        params.put(ActionParams.REASON, reason);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_GROUP_ADD_REQUEST, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 获取登录号信息
     *
     * @return result {@link ActionData} of @{@link LoginInfoResp}
     */
    @Override
    public ActionData<LoginInfoResp> getLoginInfo() {
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_LOGIN_INFO, null);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取陌生人信息
     *
     * @param userId  QQ 号
     * @param noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     * @return result {@link ActionData} of {@link StrangerInfoResp}
     */
    @Override
    public ActionData<StrangerInfoResp> getStrangerInfo(long userId, boolean noCache) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.NO_CACHE, noCache);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_STRANGER_INFO, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取好友列表
     *
     * @return result {@link ActionList} of {@link FriendInfoResp}
     */
    @Override
    public ActionList<FriendInfoResp> getFriendList() {
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_FRIEND_LIST, null);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 删除好友
     *
     * @param friendId 好友 QQ 号
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw deleteFriend(long friendId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.USER_ID, friendId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.DELETE_FRIEND, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 获取群信息
     *
     * @param groupId 群号
     * @param noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     * @return result {@link ActionData} of {@link GroupInfoResp}
     */
    @Override
    public ActionData<GroupInfoResp> getGroupInfo(long groupId, boolean noCache) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.NO_CACHE, noCache);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GROUP_INFO, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取群列表
     *
     * @return result {@link ActionList} of {@link GroupInfoResp}
     */
    @Override
    public ActionList<GroupInfoResp> getGroupList() {
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GROUP_LIST, null);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取群成员信息
     *
     * @param groupId 群号
     * @param userId  QQ 号
     * @param noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     * @return result {@link ActionData} of {@link GroupMemberInfoResp}
     */
    @Override
    public ActionData<GroupMemberInfoResp> getGroupMemberInfo(long groupId, long userId, boolean noCache) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.NO_CACHE, noCache);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GROUP_MEMBER_INFO, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取群成员列表
     *
     * @param groupId 群号
     * @return result {@link ActionList} of {@link GroupMemberInfoResp}
     */
    @Override
    public ActionList<GroupMemberInfoResp> getGroupMemberList(long groupId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GROUP_MEMBER_LIST, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取群成员列表
     *
     * @param groupId 群号
     * @param noCache 是否不使用缓存（使用缓存可能更新不及时，但响应更快）
     * @return result {@link ActionList} of {@link GroupMemberInfoResp}
     */
    @Override
    public ActionList<GroupMemberInfoResp> getGroupMemberList(long groupId, boolean noCache) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.NO_CACHE, noCache);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GROUP_MEMBER_LIST, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * @return result {@link ActionData} of {@link VersionInfoResp}
     * @see <a href="https://docs.go-cqhttp.org/api/#%E8%8E%B7%E5%8F%96%E7%89%88%E6%9C%AC%E4%BF%A1%E6%81%AF">获取版本信息</a>
     * 获取版本信息
     */
    @Override
    public ActionData<VersionInfoResp> getVersionInfo() {

        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_VERSION_INFO, null);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取群荣誉信息
     *
     * @param groupId 群号
     * @param type    要获取的群荣誉类型, 可传入 talkative performer legend strong_newbie emotion 以分别获取单个类型的群荣誉数据, 或传入 all 获取所有数据
     * @return result {@link ActionData} of {@link GroupHonorInfoResp}
     */
    @Override
    public ActionData<GroupHonorInfoResp> getGroupHonorInfo(long groupId, String type) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.TYPE, type);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GROUP_HONOR_INFO, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 检查是否可以发送图片
     *
     * @return result {@link ActionData} of {@link BooleanResp}
     */
    @Override
    public ActionData<BooleanResp> canSendImage() {
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.CAN_SEND_IMAGE, null);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 检查是否可以发送语音
     *
     * @return result {@link ActionData} of {@link BooleanResp}
     */
    @Override
    public ActionData<BooleanResp> canSendRecord() {
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.CAN_SEND_RECORD, null);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 设置群头像
     * 目前这个API在登录一段时间后因cookie失效而失效, 请考虑后使用
     *
     * @param groupId 群号
     * @param file    图片文件名（支持绝对路径，网络URL，Base64编码）
     * @param cache   表示是否使用已缓存的文件 （通过网络URL发送时有效, 1表示使用缓存, 0关闭关闭缓存, 默认为1）
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setGroupPortrait(long groupId, String file, int cache) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.FILE, file);
        params.put(ActionParams.CACHE, cache);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_GROUP_PORTRAIT, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 检查链接安全性
     * 安全等级, 1: 安全 2: 未知 3: 危险
     *
     * @param url 需要检查的链接
     * @return result {@link ActionData} of {@link CheckUrlSafelyResp}
     */
    @Override
    public ActionData<CheckUrlSafelyResp> checkUrlSafely(String url) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.URL, url);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.CHECK_URL_SAFELY, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 发送群公告
     *
     * @param groupId 群号
     * @param content 公告内容
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw sendGroupNotice(long groupId, String content) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.CONTENT, content);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEN_GROUP_NOTICE, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 获取群 @全体成员 剩余次数
     *
     * @param groupId 群号
     * @return result {@link ActionData} of {@link GroupAtAllRemainResp}
     */
    @Override
    public ActionData<GroupAtAllRemainResp> getGroupAtAllRemain(long groupId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GROUP_AT_ALL_REMAIN, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
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
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw uploadGroupFile(long groupId, String file, String name, String folder) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.FILE, file);
        params.put(ActionParams.NAME, name);
        params.put(ActionParams.FOLDER, folder);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.UPLOAD_GROUP_FILE, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 上传群文件
     * 在不提供 folder 参数的情况下默认上传到根目录
     * 只能上传本地文件, 需要上传 http 文件的话请先下载到本地
     *
     * @param groupId 群号
     * @param file    本地文件路径
     * @param name    储存名称
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw uploadGroupFile(long groupId, String file, String name) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put("file", file);
        params.put(ActionParams.NAME, name);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.UPLOAD_GROUP_FILE, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 群组匿名用户禁言
     *
     * @param groupId   群号
     * @param anonymous 要禁言的匿名用户对象（群消息上报的 anonymous 字段）
     * @param duration  禁言时长，单位秒，无法取消匿名用户禁言
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setGroupAnonymousBan(long groupId, Anonymous anonymous, int duration) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.ANONYMOUS, anonymous);
        params.put(ActionParams.DURATION, duration);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_GROUP_ANONYMOUS_BAN, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 群组匿名用户禁言
     *
     * @param groupId  群号
     * @param flag     要禁言的匿名用户的 flag（需从群消息上报的数据中获得）
     * @param duration 禁言时长，单位秒，无法取消匿名用户禁言
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setGroupAnonymousBan(long groupId, String flag, int duration) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.FLAG, flag);
        params.put(ActionParams.DURATION, duration);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_GROUP_ANONYMOUS_BAN, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 调用 go cq http 下载文件
     *
     * @param url         链接地址
     * @param threadCount 下载线程数
     * @param headers     自定义请求头
     * @return result {@link ActionData} of {@link DownloadFileResp}
     */
    @Override
    public ActionData<DownloadFileResp> downloadFile(String url, int threadCount, String headers) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.URL, url);
        params.put(ActionParams.HEADERS, headers);
        params.put(ActionParams.THREAD_COUNT, threadCount);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.DOWNLOAD_FILE, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 调用 go cq http 下载文件
     *
     * @param url 链接地址
     * @return result {@link ActionData} of {@link DownloadFileResp}
     */
    @Override
    public ActionData<DownloadFileResp> downloadFile(String url) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.URL, url);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.DOWNLOAD_FILE, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;

    }

    /**
     * 发送合并转发 (群)
     *
     * @param groupId 群号
     * @param msg     自定义转发消息 (可使用 ShiroUtils.generateForwardMsg() 方法创建)
     *                <a href="https://docs.go-cqhttp.org/cqcode/#%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91">参考文档</a>
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionData<MsgId> sendGroupForwardMsg(long groupId, List<Map<String, Object>> msg) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.MESSAGES, msg);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_GROUP_FORWARD_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取群根目录文件列表
     *
     * @param groupId 群号
     * @return result {@link ActionData} of {@link GroupFilesResp}
     */
    @Override
    public ActionData<GroupFilesResp> getGroupRootFiles(long groupId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GROUP_ROOT_FILES, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取群子目录文件列表
     *
     * @param groupId  群号
     * @param folderId 文件夹ID 参考 Folder 对象
     * @return result {@link ActionData} of {@link GroupFilesResp}
     */
    @Override
    public ActionData<GroupFilesResp> getGroupFilesByFolder(long groupId, String folderId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.FOLDER_ID, folderId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GROUP_FILES_BY_FOLDER, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取精华消息列表
     *
     * @param groupId 群号
     * @return result {@link ActionList} of {@link EssenceMsgResp}
     */
    @Override
    public ActionList<EssenceMsgResp> getEssenceMsgList(long groupId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_ESSENCE_MSG_LIST, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 设置精华消息
     *
     * @param msgId 消息 ID
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setEssenceMsg(int msgId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.MESSAGE_ID, msgId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_ESSENCE_MSG, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 移出精华消息
     *
     * @param msgId 消息 ID
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw deleteEssenceMsg(int msgId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.MESSAGE_ID, msgId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.DELETE_ESSENCE_MSG, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 设置机器人账号资料
     *
     * @param nickname     昵称
     * @param company      公司
     * @param email        邮箱
     * @param college      学校
     * @param personalNote 个性签名
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setBotProfile(String nickname, String company, String email, String college, String personalNote) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.NICKNAME, nickname);
        params.put(ActionParams.COMPANY, company);
        params.put(ActionParams.EMAIL, email);
        params.put(ActionParams.COLLEGE, college);
        params.put(ActionParams.PERSONAL_NOTE, personalNote);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_QQ_PROFILE, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 发送合并转发 (私聊)
     *
     * @param userId 目标用户
     * @param msg    自定义转发消息 (可使用 ShiroUtils.generateForwardMsg() 方法创建)
     *               <a href="https://docs.go-cqhttp.org/cqcode/#%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91">参考文档</a>
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionData<MsgId> sendPrivateForwardMsg(long userId, List<Map<String, Object>> msg) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.MESSAGES, msg);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_PRIVATE_FORWARD_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 发送合并转发
     *
     * @param event 事件
     * @param msg   自定义转发消息 (可使用 ShiroUtils.generateForwardMsg() 方法创建)
     *              <a href="https://docs.go-cqhttp.org/cqcode/#%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91">参考文档</a>
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionData<MsgId> sendForwardMsg(AnyMessageEvent event, List<Map<String, Object>> msg) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.MESSAGES, msg);
        if (ActionParams.GROUP.equals(event.getMessageType())) {
            params.put(ActionParams.GROUP_ID, event.getGroupId());
        }
        if (ActionParams.PRIVATE.equals(event.getMessageType())) {
            params.put(ActionParams.USER_ID, event.getUserId());
        }
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_FORWARD_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 发送群聊嵌套聊天记录
     *
     * @param groupId 为要发送的群聊
     * @param msg     为消息记录
     * @param prompt  为在外部消息列表显示的文字
     * @param source  为顶部文本
     * @param summary 为底部文本
     * @param news    为外显的摘要消息，最多三条；内容的构建参考消息节点。一般来说key为text,value为文本内容
     *                <p>参考 {@link com.mikuac.shiro.common.utils.ShiroUtils#generateSingleMsg(long, String, String)}</p>来生成单条聊天记录
     */
    public ActionData<MsgId> sendGroupForwardMsg(long groupId, List<Map<String, Object>> msg, String prompt, String source, String summary, List<Map<String, String>> news) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.MESSAGES, msg);
        params.put(ActionParams.NEWS, news);
        params.put(ActionParams.PROMPT, prompt);
        params.put(ActionParams.SOURCE, source);
        params.put(ActionParams.SUMMARY, summary);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_GROUP_FORWARD_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 发送私聊嵌套聊天记录
     *
     * @param userId  为要发送的用户
     * @param msg     为消息记录
     * @param prompt  为在外部消息列表显示的文字
     * @param source  为顶部文本
     * @param summary 为底部文本
     * @param news    为外显的摘要消息，最多三条；内容的构建参考消息节点。一般来说key为text,value为文本内容
     *                <p>参考 {@link com.mikuac.shiro.common.utils.ShiroUtils#generateSingleMsg(long, String, String)}</p>来生成单条聊天记录
     */
    public ActionData<MsgId> sendPrivateForwardMsg(long userId, List<Map<String, Object>> msg, String prompt, String source, String summary, List<Map<String, String>> news) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.MESSAGES, msg);
        params.put(ActionParams.NEWS, news);
        params.put(ActionParams.PROMPT, prompt);
        params.put(ActionParams.SOURCE, source);
        params.put(ActionParams.SUMMARY, summary);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_PRIVATE_FORWARD_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取中文分词
     *
     * @param content 内容
     * @return result {@link ActionData} of {@link WordSlicesResp}
     */
    @Override
    public ActionData<WordSlicesResp> getWordSlices(String content) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.CONTENT, content);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_WORD_SLICES, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取当前账号在线客户端列表
     *
     * @param noCache 是否无视缓存
     * @return result {@link ActionData} of {@link ClientsResp}
     */
    @Override
    public ActionData<ClientsResp> getOnlineClients(boolean noCache) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.NO_CACHE, noCache);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_ONLINE_CLIENTS, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 图片 OCR
     *
     * @param image 图片ID
     * @return result {@link ActionData} of {@link OcrResp}
     */
    public ActionData<OcrResp> ocrImage(String image) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.IMAGE, image);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.OCR_IMAGE, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 私聊发送文件
     *
     * @param userId 目标用户
     * @param file   本地文件路径
     * @param name   文件名
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw uploadPrivateFile(long userId, String file, String name) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.FILE, file);
        params.put(ActionParams.NAME, name);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.UPLOAD_PRIVATE_FILE, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 群打卡
     *
     * @param groupId 群号
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw sendGroupSign(long groupId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_GROUP_SIGN, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 删除单向好友
     *
     * @param userId QQ号
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw deleteUnidirectionalFriend(long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.USER_ID, userId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.DELETE_UNIDIRECTIONAL_FRIEND, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 获取单向好友列表
     *
     * @return result {@link ActionList} of {@link UnidirectionalFriendListResp}
     */
    @Override
    public ActionList<UnidirectionalFriendListResp> getUnidirectionalFriendList() {
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_UNIDIRECTIONAL_FRIEND_LIST, null);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取群文件资源链接
     *
     * @param groupId 群号
     * @param fileId  文件ID
     * @param busId   文件类型
     * @return result {@link ActionData} of {@link UrlResp}
     */
    @Override
    public ActionData<UrlResp> getGroupFileUrl(long groupId, String fileId, int busId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.FILE_ID, fileId);
        params.put(ActionParams.BUS_ID, busId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GROUP_FILE_URL, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取群文件资源链接
     *
     * @param groupId 群号
     * @param fileId  文件ID
     * @param busId   文件类型
     * @return result {@link ActionData} of {@link UrlResp}
     */
    @Override
    public ActionData<GroupFilesResp> getFile(long groupId, String fileId, int busId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.FILE_ID, fileId);
        params.put(ActionParams.BUS_ID, busId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_FILE, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 创建群文件文件夹
     *
     * @param groupId    群号
     * @param folderName 文件夹名
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw createGroupFileFolder(long groupId, String folderName) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.NAME, folderName);
        // 仅能在根目录创建文件夹
        params.put(ActionParams.PARENT_ID, "/");
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.CREATE_GROUP_FILE_FOLDER, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 删除群文件文件夹
     *
     * @param groupId  群号
     * @param folderId 文件夹ID
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw deleteGroupFileFolder(long groupId, String folderId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.FOLDER_ID, folderId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.DELETE_GROUP_FOLDER, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 删除群文件
     *
     * @param groupId 群号
     * @param fileId  文件ID
     * @param busId   文件类型
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw deleteGroupFile(long groupId, String fileId, int busId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.FILE_ID, fileId);
        params.put(ActionParams.BUS_ID, busId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.DELETE_GROUP_FILE, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 好友点赞
     *
     * @param userId 目标用户
     * @param times  点赞次数（每个好友每天最多 10 次，机器人为 Super VIP 则提高到 20次）
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw sendLike(long userId, int times) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.USER_ID, userId);
        params.put(ActionParams.TIMES, times);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_LIKE, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 获取状态
     *
     * @return result {@link GetStatusResp}
     */
    @Override
    public ActionData<GetStatusResp> getStatus() {
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_STATUS, null);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取收藏表情
     *
     * @return 表情的下载 URL
     */
    @Override
    public ActionList<String> fetchCustomFace() {
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.FETCH_CUSTOM_FACE, null);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取合并转发消息Id
     *
     * @param msg 自定义转发消息 (可使用 ShiroUtils.generateForwardMsg() 方法创建)
     * @return result {@link ActionData} of {@link String} 合并转发的消息Id
     */
    @Override
    public ActionData<String> sendForwardMsg(List<Map<String, Object>> msg) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.MESSAGES, msg);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SEND_FORWARD_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取群聊历史消息记录
     *
     * @param groupId      指定的群聊
     * @param messageSeq   指定的消息id
     * @param count        获取的消息条数
     * @param reverseOrder 是否反转获取到的消息,false：返回的消息为正序；true:返回的消息为倒序
     * @return 返回的消息列表
     */

    @Override
    public ActionData<GetMsgListResp> getGroupMsgHistory(long groupId, Long messageSeq, int count, boolean reverseOrder) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        if (messageSeq != null) params.put(ActionParams.MESSAGE_SEQ, messageSeq);
        params.put(ActionParams.COUNT, count);
        params.put(ActionParams.REVERSE_ORDER, reverseOrder);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_GROUP_MSG_HISTORY, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 获取好友历史消息记录
     *
     * @param userId       指定的好友
     * @param messageSeq   指定的消息id
     * @param count        获取的消息条数
     * @param reverseOrder 是否反转获取到的消息,false：返回的消息为正序；true:返回的消息为倒序
     * @return 返回的消息列表
     */
    @Override
    public ActionData<GetMsgListResp> getFriendMsgHistory(long userId, Long messageSeq, int count, boolean reverseOrder) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.USER_ID, userId);
        if (messageSeq != null) params.put(ActionParams.MESSAGE_SEQ, messageSeq);
        params.put(ActionParams.COUNT, count);
        params.put(ActionParams.REVERSE_ORDER, reverseOrder);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_FRIEND_MSG_HISTORY, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    private ActionRaw doPoke(ActionPathEnum path, Consumer<Map<String, Object>> paramFiller) {
        Map<String, Object> params = new HashMap<>();
        paramFiller.accept(params);
        JsonObjectWrapper result = actionHandler.action(session, path, params);
        return result != null
                ? result.to(ActionRaw.class)
                : null;
    }

    @Override
    public ActionRaw sendFriendPoke(long userId) {
        return doPoke(ActionPathEnum.FRIEND_POKE,
                p -> p.put(ActionParams.USER_ID, userId));
    }

    @Override
    public ActionRaw sendFriendPoke(long userId, long targetId) {
        return doPoke(ActionPathEnum.FRIEND_POKE, p -> {
            p.put(ActionParams.USER_ID, userId);
            p.put(ActionParams.TARGET_ID, targetId);
        });
    }

    @Override
    public ActionRaw sendGroupPoke(long groupId, long userId) {
        return doPoke(ActionPathEnum.GROUP_POKE, p -> {
            p.put(ActionParams.GROUP_ID, groupId);
            p.put(ActionParams.USER_ID, userId);
        });
    }

    /**
     * 设置群消息表情回应
     *
     * @param groupId 群号
     * @param msgId   消息 ID
     * @param code    表情 ID
     * @param isAdd   添加/取消 回应
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setGroupReaction(long groupId, int msgId, String code, boolean isAdd) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.GROUP_ID, groupId);
        params.put(ActionParams.MESSAGE_ID, msgId);
        params.put(ActionParams.CODE, code);
        params.put(ActionParams.IS_ADD, isAdd);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_GROUP_REACTION, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 设置消息表情回应(贴表情)
     *
     * @param msgId 消息 ID
     * @param code  表情 ID
     * @param isSet 添加/取消 回应
     * @return result {@link ActionRaw}
     */
    @Override
    public ActionRaw setMsgEmojiLike(int msgId, String code, boolean isSet) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.MESSAGE_ID, msgId);
        params.put(ActionParams.EMOJI_ID, code);
        params.put(ActionParams.SET, isSet);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.SET_MSG_EMOJI_LIKE, params);
        return result != null ? result.to(ActionRaw.class) : null;
    }

    /**
     * 获取合并转发消息
     *
     * @param msgId 消息ID
     * @return result {@link ActionData} of {@link GetForwardMsgResp}
     */
    public ActionData<GetForwardMsgResp> getForwardMsg(int msgId) {
        Map<String, Object> params = new HashMap<>();
        params.put(ActionParams.MESSAGE_ID, msgId);
        JsonObjectWrapper result = actionHandler.action(session, ActionPathEnum.GET_FORWARD_MSG, params);
        return result != null ? JsonUtils.readValue(result.toJSONString(), new TypeReference<>() {
        }) : null;
    }

    /**
     * 自定义请求
     *
     * @param action 请求路径
     * @param params 请求参数
     * @return result {@link ActionData}
     */
    @SuppressWarnings("rawtypes")
    public ActionData customRequest(ActionPath action, Map<String, Object> params) {
        JsonObjectWrapper result = actionHandler.action(session, action, params);
        return result != null ? result.to(ActionData.class) : null;
    }

    /**
     * 自定义请求
     *
     * @param action 请求路径
     * @param params 请求参数
     * @return result {@link ActionData}
     */
    public <T> ActionData<T> customRequest(ActionPath action, Map<String, Object> params, Class<T> clazz) {
        JsonObjectWrapper result = actionHandler.action(session, action, params);
        try {
            return result != null ? JsonUtils.getObjectMapper().readValue(result.toJSONString(),
                    JsonUtils.getObjectMapper().getTypeFactory().constructParametricType(ActionData.class, clazz)) : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 自定义请求
     *
     * @param action 请求路径
     * @param params 请求参数
     * @return result {@link ActionData}
     */
    @SuppressWarnings("rawtypes")
    public ActionData customRawRequest(ActionPath action, Map<String, Object> params) {
        JsonObjectWrapper result = actionHandler.rawAction(session, action, params);
        return result != null ? result.to(ActionData.class) : null;
    }

    /**
     * 自定义请求
     *
     * @param action 请求路径
     * @param params 请求参数
     * @return result {@link ActionData}
     */
    public <T> ActionData<T> customRawRequest(ActionPath action, Map<String, Object> params, Class<T> clazz) {
        JsonObjectWrapper result = actionHandler.rawAction(session, action, params);
        try {
            return result != null ? JsonUtils.getObjectMapper().readValue(result.toJSONString(),
                    JsonUtils.getObjectMapper().getTypeFactory().constructParametricType(ActionData.class, clazz)) : null;
        } catch (Exception e) {
            return null;
        }
    }

}
