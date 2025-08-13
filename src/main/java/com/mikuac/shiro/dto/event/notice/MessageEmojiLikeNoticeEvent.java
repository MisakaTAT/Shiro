package com.mikuac.shiro.dto.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


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
    @JsonProperty("operator_id")
    private Long operatorId;

    /**
     * 表情ID
     */
    @JsonProperty("code")
    private String code;

    /**
     * 表情数量
     */
    @JsonProperty("count")
    private Integer count;

}
