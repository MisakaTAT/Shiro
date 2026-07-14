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
     *
     * @deprecated 请改用 {@link #userId}
     */
    @SuppressWarnings("java:S1133")
    @Deprecated(since = "2.5.4", forRemoval = false)
    @JsonProperty("operator_id")
    private Long operatorId;

    /**
     * 操作者ID
     */
    @JsonProperty("user_id")
    private Long userId;

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

    /**
     * 获取操作者ID。
     *
     * @return 优先返回 {@code operatorId}，否则返回 {@code userId}
     * @deprecated 请改用 {@link #getUserId()}
     */
    @SuppressWarnings("java:S1133")
    @Deprecated(since = "2.5.4", forRemoval = false)
    public Long getOperatorId() {
        return operatorId != null ? operatorId : userId;
    }

    @Override
    public Long getUserId() {
        return userId != null ? userId : operatorId;
    }

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
