package com.mikuac.shiro.handler.event;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.notice.*;
import com.mikuac.shiro.handler.Handler;
import com.mikuac.shiro.handler.injection.InjectionHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zero
 */
@Component
public class NoticeHandler {

    @Resource
    private Handler handler;

    @Resource
    private InjectionHandler injection;

    @Resource
    private EventHandler event;

    public void groupUpload(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupUploadNoticeEvent event = eventJson.to(GroupUploadNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onGroupUploadNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void groupAdmin(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupAdminNoticeEvent event = eventJson.to(GroupAdminNoticeEvent.class);
        injection.invokeGroupAdmin(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onGroupAdminNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void groupDecrease(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupDecreaseNoticeEvent event = eventJson.to(GroupDecreaseNoticeEvent.class);
        injection.invokeGroupDecrease(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onGroupDecreaseNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void groupIncrease(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupIncreaseNoticeEvent event = eventJson.to(GroupIncreaseNoticeEvent.class);
        injection.invokeGroupIncrease(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onGroupIncreaseNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void groupBan(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupBanNoticeEvent event = eventJson.to(GroupBanNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onGroupBanNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void friendAdd(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        FriendAddNoticeEvent event = eventJson.to(FriendAddNoticeEvent.class);
        injection.invokeFriendAdd(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onFriendAddNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void groupMsgDelete(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupMsgDeleteNoticeEvent event = eventJson.to(GroupMsgDeleteNoticeEvent.class);
        injection.invokeGroupRecall(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onGroupMsgDeleteNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void privateMsgDelete(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        PrivateMsgDeleteNoticeEvent event = eventJson.to(PrivateMsgDeleteNoticeEvent.class);
        injection.invokeFriendRecall(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onPrivateMsgDeleteNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void groupCardChange(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        GroupCardChangeNotice event = eventJson.to(GroupCardChangeNotice.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onGroupCardChangeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void offlineFile(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        ReceiveOfflineFilesNoticeEvent event = eventJson.to(ReceiveOfflineFilesNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onReceiveOfflineFilesNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void channelCreated(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        ChannelCreatedNoticeEvent event = eventJson.to(ChannelCreatedNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onChannelCreatedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void channelDestroyed(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        ChannelDestroyedNoticeEvent event = eventJson.to(ChannelDestroyedNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onChannelDestroyedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void channelUpdated(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        ChannelUpdatedNoticeEvent event = eventJson.to(ChannelUpdatedNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onChannelUpdatedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void messageReactionsUpdated(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        MessageReactionsUpdatedNoticeEvent event = eventJson.to(MessageReactionsUpdatedNoticeEvent.class);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (handler.getPlugin(pluginClass).onMessageReactionsUpdatedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    public void notify(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        event.notify(bot, eventJson);
    }

}
