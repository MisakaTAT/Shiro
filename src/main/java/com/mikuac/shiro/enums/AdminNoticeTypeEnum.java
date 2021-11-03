package com.mikuac.shiro.enums;

/**
 * 群管理员变动类型枚举
 *
 * @author meme
 * @version V0.0.1
 * @Package com.mikuac.shiro.enums
 * @date 2021/10/26 21:24
 */

public enum AdminNoticeTypeEnum {

    /**
     * 所有类型都通知
     */
    ALL("ALL"),

    /**
     * 所有类型都不通知
     */
    OFF("OFF"),

    /**
     * 仅通知设置管理员
     */
    SET("SET"),

    /**
     * 仅通知取消管理员
     */
    UNSET("UNSET");

    AdminNoticeTypeEnum(String value) {
    }

}
