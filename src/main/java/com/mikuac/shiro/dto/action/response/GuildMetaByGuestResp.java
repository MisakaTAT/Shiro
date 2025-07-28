package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>GuildMetaByGuestResp class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class GuildMetaByGuestResp {

    @JsonProperty("guild_id")
    private String guildId;

    @JsonProperty("guild_name")
    private String guildName;

    @JsonProperty("guild_profile")
    private String guildProfile;

    @JsonProperty("create_time")
    private Long createTime;

    @JsonProperty("max_member_count")
    private Long maxMemberCount;

    @JsonProperty("max_robot_count")
    private Long maxRobotCount;

    @JsonProperty("max_admin_count")
    private Long maxAdminCount;

    @JsonProperty("member_count")
    private Long memberCount;

    @JsonProperty("owner_id")
    private String ownerId;

}
