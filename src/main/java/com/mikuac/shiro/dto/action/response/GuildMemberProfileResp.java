package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>GuildMemberProfileResp class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class GuildMemberProfileResp {

    @JsonProperty("tiny_id")
    private String tinyId;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("join_time")
    private Long joinTime;

    @JsonProperty("roles")
    private List<RoleInfo> roles;

    @Data
    private static class RoleInfo {

        @JsonProperty("role_id")
        private String roleId;

        @JsonProperty("role_name")
        private String roleName;

    }

}
