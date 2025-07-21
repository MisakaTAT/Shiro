package com.mikuac.shiro.dto.event.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 频道消息
 *
 * @author Alexskim
 * @version $Id: $Id
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class GuildMessageEvent extends MessageEvent {

    @JsonProperty("message_id")
    private String messageId;

    @JsonProperty("post_type")
    private String postType;

    @JsonProperty("sub_type")
    private String subType;

    @JsonProperty("guild_id")
    private String guildId;

    @JsonProperty("channel_id")
    private String channelId;

    @JsonProperty("self_tiny_id")
    private String selfTinyId;

    @JsonProperty("time")
    private Long time;

    @JsonProperty("sender")
    private Sender sender;

    /**
     * Sender Info
     */
    @Data
    public static class Sender {

        @JsonProperty("user_id")
        private Long userId;

        @JsonProperty("tiny_id")
        private String tinyId;

        @JsonProperty("nickname")
        private String nickname;

    }

}
