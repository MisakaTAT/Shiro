package com.mikuac.shiro.dto.event.notice;

import com.alibaba.fastjson2.annotation.JSONField;
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
    @JSONField(name = "guild_id")
    private String guildId;

    /**
     * 子频道ID
     */
    @JSONField(name = "channel_id")
    private String channelId;

    /**
     * 消息ID
     */
    @JSONField(name = "message_id")
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
        @JSONField(name = "emoji_id")
        private String emojiId;

        /**
         * 表情对应数值ID
         */
        @JSONField(name = "emoji_index")
        private int emojiIndex;

        /**
         * 表情类型
         */
        @JSONField(name = "emoji_type")
        private int emojiType;

        /**
         * 表情名字
         */
        @JSONField(name = "emoji_name")
        private String emojiName;

        /**
         * 当前表情被贴数量
         */
        @JSONField(name = "count")
        private int count;

        /**
         * BOT是否点击
         */
        @JSONField(name = "clicked")
        private boolean clicked;

    }

}
