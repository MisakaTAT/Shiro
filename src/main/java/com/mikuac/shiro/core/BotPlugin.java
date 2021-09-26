package com.mikuac.shiro.core;

import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.dto.event.notice.*;
import com.mikuac.shiro.dto.event.request.FriendAddRequestEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author Zero
 */
@Component
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
     * 收到私聊消息
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onPrivateMessage(@NotNull Bot bot, @NotNull PrivateMessageEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 收到群聊消息
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群文件上传事件
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupUploadNotice(@NotNull Bot bot, @NotNull GroupUploadNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 管理员变动
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupAdminNotice(@NotNull Bot bot, @NotNull GroupAdminNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 退群事件
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupDecreaseNotice(@NotNull Bot bot, @NotNull GroupDecreaseNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群成员增加事件
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupIncreaseNotice(@NotNull Bot bot, @NotNull GroupIncreaseNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群禁言事件
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupBanNotice(@NotNull Bot bot, @NotNull GroupBanNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 好友添加请求
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onFriendAddNotice(@NotNull Bot bot, @NotNull FriendAddNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群消息撤回事件
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupMsgDeleteNotice(@NotNull Bot bot, @NotNull GroupMsgDeleteNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 私聊消息撤回事件
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onPrivateMsgDeleteNotice(@NotNull Bot bot, @NotNull PrivateMsgDeleteNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群戳一戳事件
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupPokeNotice(@NotNull Bot bot, @NotNull PokeNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 私聊戳一戳事件
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onPrivatePokeNotice(@NotNull Bot bot, @NotNull PokeNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群红包运气王事件
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupLuckyKingNotice(@NotNull Bot bot, @NotNull GroupLuckyKingNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群荣誉变动事件
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupHonorChangeNotice(@NotNull Bot bot, @NotNull GroupHonorChangeNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 群名片修改事件
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupCardChangeNotice(@NotNull Bot bot, @NotNull GroupCardChangeNotice event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 收到离线文件
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onReceiveOfflineFilesNotice(@NotNull Bot bot, @NotNull ReceiveOfflineFilesNoticeEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 好友添加请求
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onFriendAddRequest(@NotNull Bot bot, @NotNull FriendAddRequestEvent event) {
        return MESSAGE_IGNORE;
    }

    /**
     * 加群请求
     *
     * @param bot   bot对象
     * @param event 上报事件
     * @return 是否执行下一个插件，MESSAGE_IGNORE 向下执行，MESSAGE_BLOCK 不向下执行
     */
    public int onGroupAddRequest(@NotNull Bot bot, @NotNull GroupAddRequestEvent event) {
        return MESSAGE_IGNORE;
    }

}
