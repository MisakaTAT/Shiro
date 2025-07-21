package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>GuildServiceProfileResp class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class GuildServiceProfileResp {

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("tiny_id")
    private String tinyId;

    @JsonProperty("avatar_url")
    private String avatarUrl;

}
