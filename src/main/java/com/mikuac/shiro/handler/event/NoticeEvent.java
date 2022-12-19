package com.mikuac.shiro.handler.event;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.common.utils.EventUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.notice.*;
import com.mikuac.shiro.enums.NoticeEventEnum;
import com.mikuac.shiro.handler.injection.InjectionHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author zero
 */
@Component
public class NoticeEvent {

    @Resource
    private EventUtils utils;

    @Resource
    private InjectionHandler injection;

    @Resource
    private NotifyEvent notify;

    /**
     * 存储通知事件处理器
     */
    public final Map<String, BiConsumer<Bot, JSONObject>> handlers = new HashMap<>();

    /**
     * 通知事件分发
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void handler(@NotNull Bot bot, @NotNull JSONObject resp) {
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
     * @param resp {@link JSONObject}
     * @param type {@link NoticeEventEnum}
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void process(@NotNull Bot bot, JSONObject resp, NoticeEventEnum type) {
        bot.getPluginList().stream().anyMatch(o -> {
            int status = BotPlugin.MESSAGE_IGNORE;

            if (type == NoticeEventEnum.GROUP_UPLOAD) {
                status = utils.getPlugin(o).onGroupUploadNotice(bot, resp.to(GroupUploadNoticeEvent.class));
            }

            if (type == NoticeEventEnum.GROUP_ADMIN) {
                GroupAdminNoticeEvent event = resp.to(GroupAdminNoticeEvent.class);
                injection.invokeGroupAdmin(bot, event);
                status = utils.getPlugin(o).onGroupAdminNotice(bot, event);
            }

            if (type == NoticeEventEnum.GROUP_DECREASE) {
                GroupDecreaseNoticeEvent event = resp.to(GroupDecreaseNoticeEvent.class);
                injection.invokeGroupDecrease(bot, event);
                status = utils.getPlugin(o).onGroupDecreaseNotice(bot, event);
            }

            if (type == NoticeEventEnum.GROUP_INCREASE) {
                GroupIncreaseNoticeEvent event = resp.to(GroupIncreaseNoticeEvent.class);
                injection.invokeGroupIncrease(bot, event);
                status = utils.getPlugin(o).onGroupIncreaseNotice(bot, event);
            }

            if (type == NoticeEventEnum.GROUP_BAN) {
                status = utils.getPlugin(o).onGroupBanNotice(bot, resp.to(GroupBanNoticeEvent.class));
            }

            if (type == NoticeEventEnum.FRIEND_ADD) {
                FriendAddNoticeEvent event = resp.to(FriendAddNoticeEvent.class);
                injection.invokeFriendAdd(bot, event);
                status = utils.getPlugin(o).onFriendAddNotice(bot, event);
            }

            if (type == NoticeEventEnum.GROUP_MSG_DELETE) {
                GroupMsgDeleteNoticeEvent event = resp.to(GroupMsgDeleteNoticeEvent.class);
                injection.invokeGroupRecall(bot, event);
                status = utils.getPlugin(o).onGroupMsgDeleteNotice(bot, event);
            }

            if (type == NoticeEventEnum.PRIVATE_MSG_DELETE) {
                PrivateMsgDeleteNoticeEvent event = resp.to(PrivateMsgDeleteNoticeEvent.class);
                injection.invokeFriendRecall(bot, event);
                status = utils.getPlugin(o).onPrivateMsgDeleteNotice(bot, event);
            }

            if (type == NoticeEventEnum.GROUP_CARD_CHANGE) {
                status = utils.getPlugin(o).onGroupCardChangeNotice(bot, resp.to(GroupCardChangeNotice.class));
            }

            if (type == NoticeEventEnum.OFFLINE_FILE) {
                status = utils.getPlugin(o).onReceiveOfflineFilesNotice(bot, resp.to(ReceiveOfflineFilesNoticeEvent.class));
            }

            if (type == NoticeEventEnum.CHANNEL_CREATED) {
                status = utils.getPlugin(o).onChannelCreatedNotice(bot, resp.to(ChannelCreatedNoticeEvent.class));
            }

            if (type == NoticeEventEnum.CHANNEL_DESTROYED) {
                status = utils.getPlugin(o).onChannelDestroyedNotice(bot, resp.to(ChannelDestroyedNoticeEvent.class));
            }

            if (type == NoticeEventEnum.CHANNEL_UPDATED) {
                status = utils.getPlugin(o).onChannelUpdatedNotice(bot, resp.to(ChannelUpdatedNoticeEvent.class));
            }

            if (type == NoticeEventEnum.MESSAGE_REACTIONS_UPDATED) {
                status = utils.getPlugin(o).onMessageReactionsUpdatedNotice(bot, resp.to(MessageReactionsUpdatedNoticeEvent.class));
            }

            return status == BotPlugin.MESSAGE_BLOCK;
        });
    }

    /**
     * 群文件上传
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void groupUpload(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NoticeEventEnum.GROUP_UPLOAD);
    }

    /**
     * 群管理员变动
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void groupAdmin(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NoticeEventEnum.GROUP_ADMIN);
    }

    /**
     * 群成员减少
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void groupDecrease(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NoticeEventEnum.GROUP_DECREASE);
    }

    /**
     * 群成员增加
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void groupIncrease(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NoticeEventEnum.GROUP_INCREASE);
    }

    /**
     * 群禁言
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void groupBan(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NoticeEventEnum.GROUP_BAN);
    }

    /**
     * 好友添加
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void friendAdd(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NoticeEventEnum.FRIEND_ADD);
    }

    /**
     * 群消息撤回
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void groupMsgDelete(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NoticeEventEnum.GROUP_MSG_DELETE);
    }

    /**
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void privateMsgDelete(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NoticeEventEnum.PRIVATE_MSG_DELETE);
    }

    /**
     * 群成员名片更新
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void groupCardChange(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NoticeEventEnum.GROUP_CARD_CHANGE);
    }

    /**
     * 接收到离线文件
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void offlineFile(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NoticeEventEnum.OFFLINE_FILE);
    }

    /**
     * 子频道创建
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void channelCreated(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NoticeEventEnum.CHANNEL_CREATED);
    }

    /**
     * 子频道删除
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void channelDestroyed(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NoticeEventEnum.CHANNEL_DESTROYED);
    }

    /**
     * 子频道信息更新
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void channelUpdated(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NoticeEventEnum.CHANNEL_UPDATED);
    }

    /**
     * 频道消息表情贴更新
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void messageReactionsUpdated(@NotNull Bot bot, @NotNull JSONObject resp) {
        process(bot, resp, NoticeEventEnum.MESSAGE_REACTIONS_UPDATED);
    }

    /**
     * 子通知事件
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void notify(@NotNull Bot bot, @NotNull JSONObject resp) {
        notify.handler(bot, resp);
    }

}
