package com.mikuac.shiro.dto.event.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("time")
    private Long interval;

    @JsonProperty("status")
    private Status status;

    @Data
    public static class Status {
        @JsonProperty("app_initialized")
        Boolean appInitialized;

        @JsonProperty("app_enabled")
        Boolean appEnabled;

        @JsonProperty("app_good")
        Boolean appIsGood;

        @JsonProperty("plugins_good")
        Boolean pluginsIsGood;

        @JsonProperty("online")
        Boolean online;

        @JsonProperty("stat")
        StatusStatistics stat;

    }

    @Data
    public static class StatusStatistics {

        @JsonProperty("packet_received")
        Long packetReceived;

        @JsonProperty("packet_sent")
        Long packetSent;

        @JsonProperty("packet_lost")
        Long packetLost;

        @JsonProperty("message_received")
        Long messageReceived;

        @JsonProperty("message_sent")
        Long messageSent;

        @JsonProperty("disconnect_times")
        Long disconnectTimes;

        @JsonProperty("lost_times")
        Long lostTimes;

        @JsonProperty("last_message_time")
        Long lastMessageTime;

    }

}
