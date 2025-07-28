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
public class FriendInfoResp {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("remark")
    private String remark;

}
