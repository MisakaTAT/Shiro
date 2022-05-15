package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import com.mikuac.shiro.dto.event.message.GuildMessageEvent;
import lombok.Data;

/**
 * @author Zero
 */
@Data
public class GetGuildMsgResp {

    @JSONField(name = "guild_id")
    private String guildId;

    @JSONField(name = "channel_id")
    private String channelId;

    @JSONField(name = "message")
    private String message;

    @JSONField(name = "message_id")
    private String messageId;

    @JSONField(name = "message_seq")
    private int messageSeq;

    @JSONField(name = "message_source")
    private String messageSource;

    @JSONField(name = "sender")
    private GuildMessageEvent.Sender sender;

    @JSONField(name = "time")
    private long time;

}
