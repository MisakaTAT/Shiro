package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mikuac.shiro.dto.event.message.GuildMessageEvent;
import lombok.Data;

/**
 * <p>GetGuildMsgResp class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class GetGuildMsgResp {

    @JsonProperty("guild_id")
    private String guildId;

    @JsonProperty("channel_id")
    private String channelId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("message_id")
    private String messageId;

    @JsonProperty("message_seq")
    private Integer messageSeq;

    @JsonProperty("message_source")
    private String messageSource;

    @JsonProperty("sender")
    private GuildMessageEvent.Sender sender;

    @JsonProperty("time")
    private Long time;

}
