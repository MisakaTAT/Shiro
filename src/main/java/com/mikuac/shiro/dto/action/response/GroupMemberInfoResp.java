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
public class GroupMemberInfoResp {

    @JsonProperty("group_id")
    private Long groupId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("card")
    private String card;

    @JsonProperty("sex")
    private String sex;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("area")
    private String area;

    @JsonProperty("join_time")
    private Integer joinTime;

    @JsonProperty("last_sent_time")
    private Integer lastSentTime;

    @JsonProperty("level")
    private String level;

    @JsonProperty("role")
    private String role;

    @JsonProperty("unfriendly")
    private Boolean unfriendly;

    @JsonProperty("title")
    private String title;

    @JsonProperty("title_expire_time")
    private Long titleExpireTime;

    @JsonProperty("card_changeable")
    private Boolean cardChangeable;

}
