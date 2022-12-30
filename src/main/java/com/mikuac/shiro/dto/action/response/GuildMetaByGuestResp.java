package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * <p>GuildMetaByGuestResp class.</p>
 *
 * @author Zero
 * @version $Id: $Id
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
    private Long createTime;

    @JSONField(name = "max_member_count")
    private Long maxMemberCount;

    @JSONField(name = "max_robot_count")
    private Long maxRobotCount;

    @JSONField(name = "max_admin_count")
    private Long maxAdminCount;

    @JSONField(name = "member_count")
    private Long memberCount;

    @JSONField(name = "owner_id")
    private String ownerId;

}
