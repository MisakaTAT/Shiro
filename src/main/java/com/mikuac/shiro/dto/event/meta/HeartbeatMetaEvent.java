package com.mikuac.shiro.dto.event.meta;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class HeartbeatMetaEvent extends MetaEvent {

    @JSONField(name = "time")
    private Long interval;

    @JSONField(name = "status")
    private Status status;

    @Data
    public static class Status {
        @JSONField(name = "app_initialized")
        Boolean appInitialized;

        @JSONField(name = "app_enabled")
        Boolean appEnabled;

        @JSONField(name = "app_good")
        Boolean appIsGood;

        @JSONField(name = "plugins_good")
        Boolean pluginsIsGood;

        @JSONField(name = "online")
        Boolean online;

        @JSONField(name = "stat")
        StatusStatistics stat;

    }

    @Data
    public static class StatusStatistics {

        @JSONField(name = "packet_received")
        Long packetReceived;

        @JSONField(name = "packet_sent")
        Long packetSent;

        @JSONField(name = "packet_lost")
        Long packetLost;

        @JSONField(name = "message_received")
        Long messageReceived;

        @JSONField(name = "message_sent")
        Long messageSent;

        @JSONField(name = "disconnect_times")
        Long disconnectTimes;

        @JSONField(name = "lost_times")
        Long lostTimes;

        @JSONField(name = "last_message_time")
        Long lastMessageTime;

    }

}
