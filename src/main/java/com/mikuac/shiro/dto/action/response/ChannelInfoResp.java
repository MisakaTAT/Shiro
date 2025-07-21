package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>ChannelInfoResp class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class ChannelInfoResp {

    /**
     * 所属频道ID
     */
    @JsonProperty("owner_guild_id")
    private String ownerGuildId;

    /**
     * 子频道ID
     */
    @JsonProperty("channel_id")
    private String channelId;

    /**
     * 子频道类型
     */
    @JsonProperty("channel_type")
    private Integer channelType;

    /**
     * 子频道名称
     */
    @JsonProperty("channel_name")
    private String channelName;

    /**
     * 创建时间
     */
    @JsonProperty("create_time")
    private Long createTime;

    /**
     * 创建者ID
     */
    @JsonProperty("creator_tiny_id")
    private String creatorTinyId;

    /**
     * 发言权限类型
     */
    @JsonProperty("talk_permission")
    private Integer talkPermission;

    /**
     * 可视性类型
     */
    @JsonProperty("visible_type")
    private Integer visibleType;

    /**
     * 当前启用的慢速模式Key
     */
    @JsonProperty("current_slow_mode")
    private Integer currentSlowMode;

    /**
     * 频道内可用慢速模式类型列表
     */
    @JsonProperty("slow_modes")
    private List<SlowModeInfo> slowModes;

    @Data
    private static class SlowModeInfo {

        /**
         * 慢速模式Key
         */
        @JsonProperty("slow_mode_key")
        private Integer slowModeKey;

        /**
         * 慢速模式说明
         */
        @JsonProperty("slow_mode_text")
        private String slowModeText;

        /**
         * 周期内发言频率限制
         */
        @JsonProperty("speak_frequency")
        private Integer speakFrequency;

        /**
         * 单位周期时间, 单位秒
         */
        @JsonProperty("slow_mode_circle")
        private Integer slowModeCircle;

    }

}
