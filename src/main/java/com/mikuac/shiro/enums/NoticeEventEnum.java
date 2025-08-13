package com.mikuac.shiro.enums;

/**
 * 通知类型枚举
 *
 * @author zero
 */
public enum NoticeEventEnum {
    /**
     * 群文件上传
     */
    GROUP_UPLOAD,
    /**
     * 管理员变动
     */
    GROUP_ADMIN,
    /**
     * 退群
     */
    GROUP_DECREASE,
    /**
     * 加群
     */
    GROUP_INCREASE,
    /**
     * 群禁言
     */
    GROUP_BAN,
    /**
     * 加好友
     */
    FRIEND_ADD,
    /**
     * 群消息撤回
     */
    GROUP_MSG_DELETE,
    /**
     * 私聊撤回
     */
    PRIVATE_MSG_DELETE,
    /**
     * 群名片变更
     */
    GROUP_CARD_CHANGE,
    /**
     * 收到离线文件
     */
    OFFLINE_FILE,
    /**
     * 子频道创建
     */
    CHANNEL_CREATED,
    /**
     * 子频道删除
     */
    CHANNEL_DESTROYED,
    /**
     * 子频道信息更新
     */
    CHANNEL_UPDATED,
    /**
     * 频道消息表情贴更新
     */
    MESSAGE_REACTIONS_UPDATED,
    /**
     * 群消息表情贴
     */
    GROUP_MESSAGE_REACTION,
    /**
     * 消息表情回应
     */
    MESSAGE_EMOJI_LIKE
}
