package com.mikuac.shiro.dto.event.message;

import com.alibaba.fastjson.annotation.JSONField;
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

    @JSONField(name = "post_type")
    private String postType;

    @JSONField(name = "sub_type")
    private String subType;

    @JSONField(name = "guild_id")
    private String guildId;

    @JSONField(name = "channel_id")
    private String channelId;

    @JSONField(name = "sender")
    private PrivateMessageEvent.PrivateSender privateSender;

    /**
     * sender信息
     */
    @Data
    public static class PrivateSender {

        @JSONField(name = "user_id")
        private long userId;

        @JSONField(name = "tiny_id")
        private String tinyId;

        @JSONField(name = "nickname")
        private String nickname;

        @JSONField(name = "sex")
        private String sex;

        @JSONField(name = "age")
        private int age;

    }
}
