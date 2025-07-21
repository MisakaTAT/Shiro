package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>UnidirectionalFriendListResp class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
public class UnidirectionalFriendListResp {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("source")
    private String source;

}
