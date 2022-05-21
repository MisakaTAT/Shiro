package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author Zero
 */
@Data
public class ChannelInfoResp {

    /**
     * 所属频道ID
     */
    @JSONField(name = "owner_guild_id")
    private String ownerGuildId;

    /**
     * 子频道ID
     */
    @JSONField(name = "channel_id")
    private String channelId;

    /**
     * 子频道类型
     */
    @JSONField(name = "channel_type")
    private int channelType;

    /**
     * 子频道名称
     */
    @JSONField(name = "channel_name")
    private String channelName;

    /**
     * 创建时间
     */
    @JSONField(name = "create_time")
    private long createTime;

    /**
     * 创建者ID
     */
    @JSONField(name = "creator_tiny_id")
    private String creatorTinyId;

    /**
     * 发言权限类型
     */
    @JSONField(name = "talk_permission")
    private int talkPermission;

    /**
     * 可视性类型
     */
    @JSONField(name = "visible_type")
    private int visibleType;

    /**
     * 当前启用的慢速模式Key
     */
    @JSONField(name = "current_slow_mode")
    private int currentSlowMode;

    /**
     * 频道内可用慢速模式类型列表
     */
    @JSONField(name = "slow_modes")
    private List<SlowModeInfo> slowModes;

    @Data
    private static class SlowModeInfo {

        /**
         * 慢速模式Key
         */
        @JSONField(name = "slow_mode_key")
        private int slowModeKey;

        /**
         * 慢速模式说明
         */
        @JSONField(name = "slow_mode_text")
        private String slowModeText;

        /**
         * 周期内发言频率限制
         */
        @JSONField(name = "speak_frequency")
        private int speakFrequency;

        /**
         * 单位周期时间, 单位秒
         */
        @JSONField(name = "slow_mode_circle")
        private int slowModeCircle;

    }

}
