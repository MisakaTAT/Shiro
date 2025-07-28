package com.mikuac.shiro.dto.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class GroupBanNoticeEvent extends NoticeEvent {

    @JsonProperty("sub_type")
    private String subType;

    @JsonProperty("group_id")
    private Long groupId;

    @JsonProperty("operator_id")
    private Long operatorId;

    @JsonProperty("duration")
    private Long duration;

    @JsonProperty("user_id")
    private Long userId;

}
