package com.mikuac.shiro.enums;

public enum SessionStatusEnum {
    /**
     * 正常在线
     */
    ONLINE,
    /**
     * 断开连接, 等待重连状态
     */
    OFFLINE,
    /**
     * 断开连接, 不会恢复
     */
    DIE
}