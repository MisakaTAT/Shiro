package com.mikuac.shiro.core;

import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.GuildMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.dto.event.notice.*;
import com.mikuac.shiro.dto.event.request.FriendAddRequestEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import org.springframework.stereotype.Component;

/**
 * <p>BotPlugin class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Component
@SuppressWarnings("unused")
public class BotPlugin {

    /**
     * 向下执行
     */
    public static final int MESSAGE_IGNORE = 0;

    /**
     * 不向下执行
     */
    public static final int MESSAGE_BLOCK = 1;

    /**
     * 全部消息监听 （群聊与私聊）
     *
     * @param bot   {@link Bot}
     * @param event {@link AnyMessageEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onAnyMessage(Bot bot, AnyMessageEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 收到私聊消息
     *
     * @param bot   {@link Bot}
     * @param event {@link PrivateMessageEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onPrivateMessage(Bot bot, PrivateMessageEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 收到群聊消息
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupMessageEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupMessage(Bot bot, GroupMessageEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 收到频道消息
     *
     * @param bot   {@link Bot}
     * @param event {@link GuildMessageEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGuildMessage(Bot bot, GuildMessageEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群文件上传事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupUploadNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupUploadNotice(Bot bot, GroupUploadNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 管理员变动
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupAdminNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupAdminNotice(Bot bot, GroupAdminNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 退群事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupDecreaseNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupDecreaseNotice(Bot bot, GroupDecreaseNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群成员增加事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupIncreaseNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupIncreaseNotice(Bot bot, GroupIncreaseNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群禁言事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupBanNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupBanNotice(Bot bot, GroupBanNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 好友添加事件
     *
     * @param bot   {@link Bot}
     * @param event {@link FriendAddNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onFriendAddNotice(Bot bot, FriendAddNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群消息撤回事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupMsgDeleteNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupMsgDeleteNotice(Bot bot, GroupMsgDeleteNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 私聊消息撤回事件
     *
     * @param bot   {@link Bot}
     * @param event {@link PrivateMsgDeleteNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onPrivateMsgDeleteNotice(Bot bot, PrivateMsgDeleteNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群戳一戳事件
     *
     * @param bot   {@link Bot}
     * @param event {@link PokeNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupPokeNotice(Bot bot, PokeNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 私聊戳一戳事件
     *
     * @param bot   {@link Bot}
     * @param event {@link PokeNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onPrivatePokeNotice(Bot bot, PokeNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群红包运气王事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupLuckyKingNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupLuckyKingNotice(Bot bot, GroupLuckyKingNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群荣誉变动事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupHonorChangeNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupHonorChangeNotice(Bot bot, GroupHonorChangeNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群名片修改事件
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupCardChangeNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupCardChangeNotice(Bot bot, GroupCardChangeNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 收到离线文件
     *
     * @param bot   {@link Bot}
     * @param event {@link ReceiveOfflineFilesNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onReceiveOfflineFilesNotice(Bot bot, ReceiveOfflineFilesNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 好友添加请求
     *
     * @param bot   {@link Bot}
     * @param event {@link FriendAddRequestEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onFriendAddRequest(Bot bot, FriendAddRequestEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 加群请求
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupAddRequestEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupAddRequest(Bot bot, GroupAddRequestEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 子频道创建
     *
     * @param bot   {@link Bot}
     * @param event {@link ChannelCreatedNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onChannelCreatedNotice(Bot bot, ChannelCreatedNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 子频道删除
     *
     * @param bot   {@link Bot}
     * @param event {@link ChannelDestroyedNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onChannelDestroyedNotice(Bot bot, ChannelDestroyedNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 子频道信息更新
     *
     * @param bot   {@link Bot}
     * @param event {@link ChannelUpdatedNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onChannelUpdatedNotice(Bot bot, ChannelUpdatedNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 频道消息表情贴更新
     *
     * @param bot   {@link Bot}
     * @param event {@link MessageReactionsUpdatedNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onMessageReactionsUpdatedNotice(Bot bot, MessageReactionsUpdatedNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群消息表情贴
     *
     * @param bot   {@link Bot}
     * @param event {@link GroupMessageReactionNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupReactionNotice(Bot bot, GroupMessageReactionNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 消息表情回应
     *
     * @param bot   {@link Bot}
     * @param event {@link MessageEmojiLikeNoticeEvent}
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onMessageEmojiLikeNotice(Bot bot, MessageEmojiLikeNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

}
