package com.mikuac.shiro.dto.event.notice;

import com.alibaba.fastjson2.annotation.JSONField;
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
    @JSONField(name = "group_id")
    private Long groupId;

    /**
     * 消息ID
     */
    @JSONField(name = "message_id")
    private Integer messageId;

    /**
     * 操作者ID
     */
    @JSONField(name = "operator_id")
    private Long operatorId;

    /**
     * 操作类型:
     * remove
     * add
     */
    @JSONField(name = "sub_type")
    private String subType;

    /**
     * 操作者ID
     */
    @JSONField(name = "code")
    private String code;

    /**
     * 操作者ID
     */
    @JSONField(name = "count")
    private Integer count;

}
