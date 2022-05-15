package com.mikuac.shiro.dto.event.message;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 频道消息
 *
 * @author Alexskim
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class GuildMessageEvent extends MessageEvent {

    @JSONField(name = "message_id")
    private String messageId;

    @JSONField(name = "post_type")
    private String postType;

    @JSONField(name = "sub_type")
    private String subType;

    @JSONField(name = "guild_id")
    private String guildId;

    @JSONField(name = "channel_id")
    private String channelId;

    @JSONField(name = "self_tiny_id")
    private String selfTinyId;

    @JSONField(name = "time")
    private long time;

    @JSONField(name = "sender")
    private Sender sender;

    /**
     * Sender Info
     */
    @Data
    public static class Sender {

        @JSONField(name = "user_id")
        private long userId;

        @JSONField(name = "tiny_id")
        private String tinyId;

        @JSONField(name = "nickname")
        private String nickname;

    }

}
