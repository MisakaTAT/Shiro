package com.mikuac.shiro.dto.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mikuac.shiro.dto.action.response.ChannelInfoResp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * <p>ChannelDestroyedNoticeEvent class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ChannelDestroyedNoticeEvent extends NoticeEvent {

    /**
     * 频道ID
     */
    @JsonProperty("guild_id")
    private String guildId;

    /**
     * 子频道ID
     */
    @JsonProperty("channel_id")
    private String channelId;

    /**
     * 操作者ID
     */
    @JsonProperty("operator_id")
    private String operatorId;

    /**
     * 频道信息
     */
    @JsonProperty("channel_info")
    private ChannelInfoResp channelInfo;

}
