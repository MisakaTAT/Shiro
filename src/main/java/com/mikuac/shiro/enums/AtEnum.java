package com.mikuac.shiro.enums;

/**
 * At枚举
 *
 * @author meme
 * @version $Id: $Id
 */
public enum AtEnum {

    /**
     * 默认值
     */
    OFF,

    /**
     * 只处理带有at机器人的消息
     */
    NEED,

    /**
     * 若消息中at了机器人此条消息会被忽略
     */
    NOT_NEED,

    /**
     * AtEnum.NEED 和 AtEnum.NOT_NEED 的合并
     */
    BOTH,

}
