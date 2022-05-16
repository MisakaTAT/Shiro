package com.mikuac.shiro.dto.event.notice;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author Zero
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ChannelUpdatedNoticeEvent extends NoticeEvent {

    /**
     * 频道ID
     */
    @JSONField(name = "guild_id")
    private String guildId;

    /**
     * 子频道ID
     */
    @JSONField(name = "channel_id")
    private String channelId;

    /**
     * 操作者ID
     */
    @JSONField(name = "operator_id")
    private String operatorId;

    /**
     * 更新前的频道信息
     */
    @JSONField(name = "old_info")
    private ChannelDestroyedNoticeEvent.ChannelInfo oldInfo;

    /**
     * 更新后的频道信息
     */
    @JSONField(name = "new_info")
    private ChannelDestroyedNoticeEvent.ChannelInfo newInfo;

}
