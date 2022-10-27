package com.mikuac.shiro.enums;

/**
 * 群管理员变动类型枚举
 *
 * @author meme
 * @version $Id: $Id
 */
public enum AdminNoticeTypeEnum {

    /**
     * 所有类型都通知
     */
    ALL,

    /**
     * 所有类型都不通知
     */
    OFF,

    /**
     * 仅通知设置管理员
     */
    SET,

    /**
     * 仅通知取消管理员
     */
    UNSET

}
