package com.mikuac.shiro.handler.event;

import com.mikuac.shiro.common.utils.EventUtils;
import com.mikuac.shiro.common.utils.JsonObjectWrapper;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.notice.*;
import com.mikuac.shiro.enums.NoticeEventEnum;
import com.mikuac.shiro.handler.injection.InjectionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author zero
 */
@Component
public class NoticeEvent {

    private final EventUtils utils;
    private final NotifyEvent notify;
    private final InjectionHandler injection;

    @Autowired
    public NoticeEvent(EventUtils eventUtils, NotifyEvent notifyEvent, InjectionHandler injectionHandler) {
        this.utils = eventUtils;
        this.notify = notifyEvent;
        this.injection = injectionHandler;
    }

    /**
     * 存储通知事件处理器
     */
    public final Map<String, BiConsumer<Bot, JsonObjectWrapper>> handlers = new HashMap<>();

    /**
     * 通知事件分发
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void handler(Bot bot, JsonObjectWrapper resp) {
        String type = resp.getString("notice_type");
        handlers.getOrDefault(
                type,
                (b, e) -> {
                }
        ).accept(bot, resp);
    }

    /**
     * 事件处理
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     * @param type {@link NoticeEventEnum}
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored", "squid:S2201"})
    private void process(Bot bot, JsonObjectWrapper resp, NoticeEventEnum type) {
        if (type == NoticeEventEnum.GROUP_UPLOAD) {
            GroupUploadNoticeEvent event = resp.to(GroupUploadNoticeEvent.class);
            injection.invokeGroupUploadNotice(bot, event);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onGroupUploadNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }

        if (type == NoticeEventEnum.GROUP_ADMIN) {
            GroupAdminNoticeEvent event = resp.to(GroupAdminNoticeEvent.class);
            injection.invokeGroupAdmin(bot, event);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onGroupAdminNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }

        if (type == NoticeEventEnum.GROUP_DECREASE) {
            GroupDecreaseNoticeEvent event = resp.to(GroupDecreaseNoticeEvent.class);
            injection.invokeGroupDecrease(bot, event);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onGroupDecreaseNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }

        if (type == NoticeEventEnum.GROUP_INCREASE) {
            GroupIncreaseNoticeEvent event = resp.to(GroupIncreaseNoticeEvent.class);
            injection.invokeGroupIncrease(bot, event);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onGroupIncreaseNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }

        if (type == NoticeEventEnum.GROUP_BAN) {
            GroupBanNoticeEvent event = resp.to(GroupBanNoticeEvent.class);
            injection.invokeGroupBanNotice(bot, event);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onGroupBanNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }

        if (type == NoticeEventEnum.FRIEND_ADD) {
            FriendAddNoticeEvent event = resp.to(FriendAddNoticeEvent.class);
            injection.invokeFriendAdd(bot, event);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onFriendAddNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }

        if (type == NoticeEventEnum.GROUP_MSG_DELETE) {
            GroupMsgDeleteNoticeEvent event = resp.to(GroupMsgDeleteNoticeEvent.class);
            injection.invokeGroupRecall(bot, event);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onGroupMsgDeleteNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }

        if (type == NoticeEventEnum.PRIVATE_MSG_DELETE) {
            PrivateMsgDeleteNoticeEvent event = resp.to(PrivateMsgDeleteNoticeEvent.class);
            injection.invokeFriendRecall(bot, event);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onPrivateMsgDeleteNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }

        if (type == NoticeEventEnum.GROUP_CARD_CHANGE) {
            GroupCardChangeNoticeEvent event = resp.to(GroupCardChangeNoticeEvent.class);
            injection.invokeGroupCardChangeNotice(bot, event);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onGroupCardChangeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }

        if (type == NoticeEventEnum.GROUP_MESSAGE_REACTION) {
            GroupMessageReactionNoticeEvent event = resp.to(GroupMessageReactionNoticeEvent.class);
            injection.invokeGroupReactionNotice(bot, event);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onGroupReactionNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }

        if (type == NoticeEventEnum.MESSAGE_EMOJI_LIKE) {
            MessageEmojiLikeNoticeEvent event = resp.to(MessageEmojiLikeNoticeEvent.class);
            injection.invokeMessageEmojiLikeNotice(bot, event);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onMessageEmojiLikeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }

        if (type == NoticeEventEnum.OFFLINE_FILE) {
            ReceiveOfflineFilesNoticeEvent event = resp.to(ReceiveOfflineFilesNoticeEvent.class);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onReceiveOfflineFilesNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }

        if (type == NoticeEventEnum.CHANNEL_CREATED) {
            ChannelCreatedNoticeEvent event = resp.to(ChannelCreatedNoticeEvent.class);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onChannelCreatedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }

        if (type == NoticeEventEnum.CHANNEL_DESTROYED) {
            ChannelDestroyedNoticeEvent event = resp.to(ChannelDestroyedNoticeEvent.class);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onChannelDestroyedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }

        if (type == NoticeEventEnum.CHANNEL_UPDATED) {
            ChannelUpdatedNoticeEvent event = resp.to(ChannelUpdatedNoticeEvent.class);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onChannelUpdatedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }

        if (type == NoticeEventEnum.MESSAGE_REACTIONS_UPDATED) {
            MessageReactionsUpdatedNoticeEvent event = resp.to(MessageReactionsUpdatedNoticeEvent.class);
            bot.getPluginList().stream().anyMatch(o -> utils.getPlugin(o).onMessageReactionsUpdatedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK);
        }
    }

    /**
     * 群文件上传
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void groupUpload(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.GROUP_UPLOAD);
    }

    /**
     * 群管理员变动
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void groupAdmin(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.GROUP_ADMIN);
    }

    /**
     * 群成员减少
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void groupDecrease(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.GROUP_DECREASE);
    }

    /**
     * 群成员增加
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void groupIncrease(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.GROUP_INCREASE);
    }

    /**
     * 群禁言
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void groupBan(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.GROUP_BAN);
    }

    /**
     * 好友添加
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void friendAdd(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.FRIEND_ADD);
    }

    /**
     * 群消息撤回
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void groupMsgDelete(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.GROUP_MSG_DELETE);
    }

    /**
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void privateMsgDelete(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.PRIVATE_MSG_DELETE);
    }

    /**
     * 群成员名片更新
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void groupCardChange(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.GROUP_CARD_CHANGE);
    }

    /**
     * 接收到离线文件
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void offlineFile(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.OFFLINE_FILE);
    }

    /**
     * 子频道创建
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void channelCreated(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.CHANNEL_CREATED);
    }

    /**
     * 子频道删除
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void channelDestroyed(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.CHANNEL_DESTROYED);
    }

    /**
     * 子频道信息更新
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void channelUpdated(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.CHANNEL_UPDATED);
    }

    /**
     * 频道消息表情贴更新
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void messageReactionsUpdated(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.MESSAGE_REACTIONS_UPDATED);
    }

    /**
     * 子通知事件
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void notify(Bot bot, JsonObjectWrapper resp) {
        notify.handler(bot, resp);
    }

    /**
     * 群消息表情贴
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void groupReactionMessage(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.GROUP_MESSAGE_REACTION);
    }

    /**
     * 消息表情回应
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void messageEmojiLikeMessage(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, NoticeEventEnum.MESSAGE_EMOJI_LIKE);
    }

}
