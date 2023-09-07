package com.mikuac.shiro.enums;

public enum ReplyEnum {
    /**
     * 不处理
     */
    OFF,
    /**
     * 不带回复
     */
    NONE,
    /**
     * 回复 bot 的消息
     */
    REPLY_ME,
    /**
     * 回复任意其他人的消息
     */
    REPLY_OTHER,
    /**
     * 任意包括回复的消息
     */
    REPLY_ALL,
}
