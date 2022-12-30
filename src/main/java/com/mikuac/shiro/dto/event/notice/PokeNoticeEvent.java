package com.mikuac.shiro.dto.event.notice;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Created on 2021/7/8.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class PokeNoticeEvent extends NoticeEvent {

    @JSONField(name = "sub_type")
    private String subType;

    @JSONField(name = "self_id")
    private Long selfId;

    @JSONField(name = "sender_id")
    private Long senderId;

    @JSONField(name = "user_id")
    private Long userId;

    @JSONField(name = "target_id")
    private Long targetId;

    @JSONField(name = "group_id")
    private Long groupId;

    @JSONField(name = "time")
    private Long time;

}
