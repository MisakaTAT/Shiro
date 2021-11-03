package com.mikuac.shiro.enums;

/**
 * At枚举
 *
 * @author meme
 * @version V0.0.1
 * @Package com.mikuac.shiro.enums
 * @date 2021/10/26 21:24
 */

public enum AtEnum {

    // 默认值
    OFF,

    // 只处理带有at机器人的消息
    NEED,

    // 若消息中at了机器人此条消息会被忽略
    NOT_NEED,

}
