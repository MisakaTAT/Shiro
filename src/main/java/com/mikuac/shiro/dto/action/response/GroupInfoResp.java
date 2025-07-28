package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created on 2021/7/8.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class GroupInfoResp {

    @JsonProperty("group_id")
    private Long groupId;

    @JsonProperty("group_name")
    private String groupName;

    @JsonProperty("group_memo")
    private String groupMemo;

    @JsonProperty("group_create_time")
    private Integer groupCreateTime;

    @JsonProperty("group_level")
    private Integer groupLevel;

    @JsonProperty("member_count")
    private Integer memberCount;

    @JsonProperty("max_member_count")
    private Integer maxMemberCount;

}
