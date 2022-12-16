package com.mikuac.shiro.handler;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.bo.ArrayMsg;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotMessageEventInterceptor;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.core.DefaultBotMessageEventInterceptor;
import com.mikuac.shiro.dto.event.message.*;
import com.mikuac.shiro.dto.event.notice.*;
import com.mikuac.shiro.dto.event.request.FriendAddRequestEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import com.mikuac.shiro.handler.injection.InjectionHandler;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 事件处理器
 *
 * @author zero
 * @version $Id: $Id
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class EventHandler {

    BotPlugin defaultPlugin = new BotPlugin();

    InjectionHandler injectionHandler = new InjectionHandler();

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 元事件处理器
     *
     * @param bot       {@link com.mikuac.shiro.core.Bot}
     * @param eventJson 响应数据
     */
    public void handler(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        String postType = eventJson.getString("post_type");
        switch (postType) {
            case "meta_event": {
                handlerMetaEvent(bot, eventJson);
                break;
            }
            case "message": {
                handlerMessage(bot, eventJson);
                break;
            }
            case "notice": {
                handlerNotice(bot, eventJson);
                break;
            }
            case "request": {
                handlerRequest(bot, eventJson);
                break;
            }
            default:
        }
    }

    /**
     * 设置拦截器
     *
     * @param bot   {@link Bot}
     * @param event {@link MessageEvent}
     * @return boolean
     */
    private boolean setInterceptor(@NotNull Bot bot, @NotNull MessageEvent event) {
        try {
            return !getInterceptor(bot.getBotMessageEventInterceptor()).preHandle(bot, event);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 推送消息并返回消息链
     *
     * @param bot       {@link Bot}
     * @param eventJson {@link JSONObject}
     * @param event     {@link MessageEvent}
     * @return {@link ArrayMsg}
     */
    private List<ArrayMsg> setAnyMessageEvent(@NotNull Bot bot, @NotNull JSONObject eventJson, @NotNull MessageEvent event) {
        try {
            List<ArrayMsg> arrayMsg = ShiroUtils.stringToMsgChain(event.getMessage());
            pushAnyMessageEvent(bot, eventJson, arrayMsg);
            return arrayMsg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 消息事件处理器
     *
     * @param bot       {@link Bot}
     * @param eventJson 响应数据
     */
    private void handlerMessage(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        String messageType = eventJson.getString("message_type");
        MessageEvent messageEvent = null;
        switch (messageType) {
            case "private": {
                PrivateMessageEvent event = eventJson.to(PrivateMessageEvent.class);
                messageEvent = event;
                if (setInterceptor(bot, event)) {
                    return;
                }
                event.setArrayMsg(setAnyMessageEvent(bot, eventJson, event));
                injectionHandler.invokePrivateMessage(bot, event);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onPrivateMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "group": {
                GroupMessageEvent event = eventJson.to(GroupMessageEvent.class);
                messageEvent = event;
                if (setInterceptor(bot, event)) {
                    return;
                }
                event.setArrayMsg(setAnyMessageEvent(bot, eventJson, event));
                injectionHandler.invokeGroupMessage(bot, event);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "guild": {
                GuildMessageEvent event = eventJson.to(GuildMessageEvent.class);
                messageEvent = event;
                if (setInterceptor(bot, event)) {
                    return;
                }
                List<ArrayMsg> arrayMsg = ShiroUtils.stringToMsgChain(event.getMessage());
                event.setArrayMsg(arrayMsg);
                injectionHandler.invokeGuildMessage(bot, event);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGuildMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            default:
        }

        if (messageEvent != null) {
            try {
                getInterceptor(bot.getBotMessageEventInterceptor()).afterCompletion(bot, messageEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 提醒事件处理器
     *
     * @param bot       {@link Bot}
     * @param eventJson 响应数据
     */
    private void handlerNotice(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        String noticeType = eventJson.getString("notice_type");
        switch (noticeType) {
            case "group_upload": {
                GroupUploadNoticeEvent event = eventJson.to(GroupUploadNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupUploadNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "group_admin": {
                GroupAdminNoticeEvent event = eventJson.to(GroupAdminNoticeEvent.class);
                injectionHandler.invokeGroupAdmin(bot, event);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupAdminNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "group_decrease": {
                GroupDecreaseNoticeEvent event = eventJson.to(GroupDecreaseNoticeEvent.class);
                injectionHandler.invokeGroupDecrease(bot, event);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupDecreaseNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "group_increase": {
                GroupIncreaseNoticeEvent event = eventJson.to(GroupIncreaseNoticeEvent.class);
                injectionHandler.invokeGroupIncrease(bot, event);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupIncreaseNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "group_ban": {
                GroupBanNoticeEvent event = eventJson.to(GroupBanNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupBanNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "friend_add": {
                FriendAddNoticeEvent event = eventJson.to(FriendAddNoticeEvent.class);
                injectionHandler.invokeFriendAdd(bot, event);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onFriendAddNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "group_recall": {
                GroupMsgDeleteNoticeEvent event = eventJson.to(GroupMsgDeleteNoticeEvent.class);
                injectionHandler.invokeGroupRecall(bot, event);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupMsgDeleteNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "friend_recall": {
                PrivateMsgDeleteNoticeEvent event = eventJson.to(PrivateMsgDeleteNoticeEvent.class);
                injectionHandler.invokeFriendRecall(bot, event);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onPrivateMsgDeleteNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "group_card": {
                GroupCardChangeNotice event = eventJson.to(GroupCardChangeNotice.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupCardChangeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "offline_file": {
                ReceiveOfflineFilesNoticeEvent event = eventJson.to(ReceiveOfflineFilesNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onReceiveOfflineFilesNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "channel_created": {
                ChannelCreatedNoticeEvent event = eventJson.to(ChannelCreatedNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onChannelCreatedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "channel_destroyed": {
                ChannelDestroyedNoticeEvent event = eventJson.to(ChannelDestroyedNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onChannelDestroyedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "channel_updated": {
                ChannelUpdatedNoticeEvent event = eventJson.to(ChannelUpdatedNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onChannelUpdatedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "message_reactions_updated": {
                MessageReactionsUpdatedNoticeEvent event = eventJson.to(MessageReactionsUpdatedNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onMessageReactionsUpdatedNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "notify": {
                handlerNotify(bot, eventJson);
                break;
            }
            default:
        }
    }

    /**
     * 通知事件处理器
     *
     * @param bot       {@link Bot}
     * @param eventJson 响应数据
     */
    private void handlerNotify(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        String subType = eventJson.getString("sub_type");
        switch (subType) {
            case "poke": {
                PokeNoticeEvent event = eventJson.to(PokeNoticeEvent.class);
                // 如果群号不为空则当作群内戳一戳处理
                if (event.getGroupId() > 0L) {
                    for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                        if (getPlugin(pluginClass).onGroupPokeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                            break;
                        }
                    }
                } else {
                    for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                        if (getPlugin(pluginClass).onPrivatePokeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                            break;
                        }
                    }
                }
                break;
            }
            case "lucky_king": {
                GroupLuckyKingNoticeEvent event = eventJson.to(GroupLuckyKingNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupLuckyKingNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "honor": {
                GroupHonorChangeNoticeEvent event = eventJson.to(GroupHonorChangeNoticeEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupHonorChangeNotice(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            default:
        }
    }

    /**
     * 请求事件处理器
     *
     * @param bot       {@link Bot}
     * @param eventJson 响应数据
     */
    private void handlerRequest(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        String requestType = eventJson.getString("request_type");
        switch (requestType) {
            case "friend": {
                FriendAddRequestEvent event = eventJson.to(FriendAddRequestEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onFriendAddRequest(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            case "group": {
                GroupAddRequestEvent event = eventJson.to(GroupAddRequestEvent.class);
                for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
                    if (getPlugin(pluginClass).onGroupAddRequest(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                        break;
                    }
                }
                break;
            }
            default:
        }
    }

    private void handlerMetaEvent(@NotNull Bot bot, @NotNull JSONObject eventJson) {
        // Ignored this handler
    }

    private void pushAnyMessageEvent(@NotNull Bot bot, @NotNull JSONObject eventJson, List<ArrayMsg> arrayMsg) {
        AnyMessageEvent event = eventJson.to(AnyMessageEvent.class);
        event.setArrayMsg(arrayMsg);
        injectionHandler.invokeAnyMessage(bot, event);
        for (Class<? extends BotPlugin> pluginClass : bot.getPluginList()) {
            if (getPlugin(pluginClass).onAnyMessage(bot, event) == BotPlugin.MESSAGE_BLOCK) {
                break;
            }
        }
    }

    private BotPlugin getPlugin(Class<? extends BotPlugin> pluginClass) {
        try {
            return applicationContext.getBean(pluginClass);
        } catch (Exception e) {
            log.warn("Plugin {} skip, Please check @Component annotation", pluginClass.getSimpleName());
            return defaultPlugin;
        }
    }

    private @NotNull BotMessageEventInterceptor getInterceptor(Class<? extends BotMessageEventInterceptor> interceptorClass) {
        try {
            return applicationContext.getBean(interceptorClass);
        } catch (Exception e) {
            log.warn("Interceptor {} skip, Please check @Component annotation", interceptorClass.getSimpleName());
            return applicationContext.getBean(DefaultBotMessageEventInterceptor.class);
        }
    }

}
