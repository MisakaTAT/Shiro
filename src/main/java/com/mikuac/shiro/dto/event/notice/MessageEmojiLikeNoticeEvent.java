package com.mikuac.shiro.dto.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class MessageEmojiLikeNoticeEvent extends NoticeEvent {

    /**
     * 群组ID
     */
    @JsonProperty("group_id")
    private Long groupId;

    /**
     * 消息ID
     */
    @JsonProperty("message_id")
    private Integer messageId;

    /**
     * 操作者ID
     */
    @Deprecated
    @JsonProperty("user_id")
    private Long operatorId;

    /**
     * 表情详情
     */
    @JsonProperty("likes")
    private List<Like> likes;

    /**
     * 是否添加
     */
    @JsonProperty("is_add")
    private boolean add;

    @Data
    public static class Like {

        /**
         * 表情ID
         */
        @JsonProperty("emoji_id")
        private String emojiId;

        /**
         * 表情数量
         */
        @JsonProperty("count")
        private Integer count;

    }

}
