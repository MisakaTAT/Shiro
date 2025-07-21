package com.mikuac.shiro.dto.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * <p>MessageReactionsUpdatedNoticeEvent class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class MessageReactionsUpdatedNoticeEvent extends NoticeEvent {

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
     * 消息ID
     */
    @JsonProperty("message_id")
    private String messageId;

    /**
     * 当前消息被贴表情列表
     */
    private List<ReactionInfo> currentReactions;

    @Data
    private static class ReactionInfo {

        /**
         * 表情ID
         */
        @JsonProperty("emoji_id")
        private String emojiId;

        /**
         * 表情对应数值ID
         */
        @JsonProperty("emoji_index")
        private Integer emojiIndex;

        /**
         * 表情类型
         */
        @JsonProperty("emoji_type")
        private Integer emojiType;

        /**
         * 表情名字
         */
        @JsonProperty("emoji_name")
        private String emojiName;

        /**
         * 当前表情被贴数量
         */
        @JsonProperty("count")
        private Integer count;

        /**
         * BOT是否点击
         */
        @JsonProperty("clicked")
        private Boolean clicked;

    }

}
