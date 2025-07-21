package com.mikuac.shiro.dto.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * <p>GroupMessageReactionNoticeEvent class.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class GroupMessageReactionNoticeEvent extends NoticeEvent {

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
     * 操作类型:
     * remove
     * add
     */
    @JsonProperty("sub_type")
    private String subType;

    /**
     * 操作者ID
     */
    @JsonProperty("code")
    private String code;

    /**
     * 操作者ID
     */
    @JsonProperty("count")
    private Integer count;

}
