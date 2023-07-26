package com.mikuac.shiro.enums;

/***
 * 元事件通知
 * 由于 go-cqhttp 实现与 OneBot 有差异,目前仅实现 go-cqhttp 标准
 */
public enum MetaEventEnum {
    /***
     * 心跳包
     */
    HEARTBEAT,
    /***
     * 生命周期
     */
    LIFECYCLE,
}
