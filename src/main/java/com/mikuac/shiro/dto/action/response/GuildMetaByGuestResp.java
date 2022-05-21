package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author Zero
 */
@Data
public class GuildMetaByGuestResp {

    @JSONField(name = "guild_id")
    private String guildId;

    @JSONField(name = "guild_name")
    private String guildName;

    @JSONField(name = "guild_profile")
    private String guildProfile;

    @JSONField(name = "create_time")
    private long createTime;

    @JSONField(name = "max_member_count")
    private long maxMemberCount;

    @JSONField(name = "max_robot_count")
    private long maxRobotCount;

    @JSONField(name = "max_admin_count")
    private long maxAdminCount;

    @JSONField(name = "member_count")
    private long memberCount;

    @JSONField(name = "owner_id")
    private String ownerId;

}
