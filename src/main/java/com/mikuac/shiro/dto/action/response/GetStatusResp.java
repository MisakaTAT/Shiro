package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Neo
 * @date 2023/7/26
 */
@Data
public class GetStatusResp {

    /**
     * 原 CQHTTP 字段, 恒定为 true
     */
    @JsonProperty("app_initialized")
    private Boolean appInitialized;

    /**
     * 原 CQHTTP 字段, 恒定为 true
     */
    @JsonProperty("app_enabled")
    private Boolean appEnabled;

    /**
     * 原 CQHTTP 字段
     */
    @JsonProperty("plugins_good")
    private Boolean pluginsGood;

    /**
     * 原 CQHTTP 字段, 恒定为 true
     */
    @JsonProperty("app_good")
    private Boolean appGood;

    /**
     * 表示BOT是否在线
     */
    private Boolean online;

    /**
     * 同 online
     */
    private Boolean good;

    /**
     * 运行统计
     */
    private Statistics stat;

    @Data
    public static class Statistics {

        /**
         * 收到的数据包总数
         */
        @JsonProperty("packet_received")
        private Long packetReceived;

        /**
         * 发送的数据包总数
         */
        @JsonProperty("packet_sent")
        private Long packetSent;

        /**
         * 数据包丢失总数
         */
        @JsonProperty("packet_lost")
        private Integer packetLost;

        /**
         * 接受信息总数
         */
        @JsonProperty("message_received")
        private Long messageReceived;

        /**
         * 发送信息总数
         */
        @JsonProperty("message_sent")
        private Long messageSent;

        /**
         * TCP 链接断开次数
         */
        @JsonProperty("disconnect_times")
        private Integer disconnectTimes;

        /**
         * 账号掉线次数
         */
        @JsonProperty("lost_times")
        private Integer lostTimes;

        /**
         * 最后一条消息时间
         */
        @JsonProperty("last_message_time")
        private Long lastMessageTime;
    }
}
