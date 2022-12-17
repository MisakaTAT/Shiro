package com.mikuac.shiro.handler.event;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.common.utils.EventUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.notice.*;
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
     * @param bot       {@link Bot}
     * @param eventJson {@link JSONObject}
     */
    public void handler(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        String type = eventJson.getString("notice_type");
        handlers.getOrDefault(
                type,
                (b, e) -> {
                }
        ).accept(bot, eventJson);
    }

    public void groupUpload(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupUploadNoticeEvent event = eventJson.to(GroupUploadNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onGroupUploadNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void groupAdmin(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupAdminNoticeEvent event = eventJson.to(GroupAdminNoticeEvent.class);
        injection.invokeGroupAdmin(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onGroupAdminNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void groupDecrease(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupDecreaseNoticeEvent event = eventJson.to(GroupDecreaseNoticeEvent.class);
        injection.invokeGroupDecrease(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onGroupDecreaseNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void groupIncrease(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupIncreaseNoticeEvent event = eventJson.to(GroupIncreaseNoticeEvent.class);
        injection.invokeGroupIncrease(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onGroupIncreaseNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void groupBan(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupBanNoticeEvent event = eventJson.to(GroupBanNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onGroupBanNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void friendAdd(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        FriendAddNoticeEvent event = eventJson.to(FriendAddNoticeEvent.class);
        injection.invokeFriendAdd(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onFriendAddNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void groupMsgDelete(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupMsgDeleteNoticeEvent event = eventJson.to(GroupMsgDeleteNoticeEvent.class);
        injection.invokeGroupRecall(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onGroupMsgDeleteNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void privateMsgDelete(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        PrivateMsgDeleteNoticeEvent event = eventJson.to(PrivateMsgDeleteNoticeEvent.class);
        injection.invokeFriendRecall(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onPrivateMsgDeleteNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void groupCardChange(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupCardChangeNotice event = eventJson.to(GroupCardChangeNotice.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onGroupCardChangeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void offlineFile(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        ReceiveOfflineFilesNoticeEvent event = eventJson.to(ReceiveOfflineFilesNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onReceiveOfflineFilesNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void channelCreated(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        ChannelCreatedNoticeEvent event = eventJson.to(ChannelCreatedNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onChannelCreatedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void channelDestroyed(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        ChannelDestroyedNoticeEvent event = eventJson.to(ChannelDestroyedNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onChannelDestroyedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void channelUpdated(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        ChannelUpdatedNoticeEvent event = eventJson.to(ChannelUpdatedNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onChannelUpdatedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void messageReactionsUpdated(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        MessageReactionsUpdatedNoticeEvent event = eventJson.to(MessageReactionsUpdatedNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (utils.getPlugin(pluginClass).onMessageReactionsUpdatedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void notify(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        notify.handler(bot, eventJson);
    }

}
