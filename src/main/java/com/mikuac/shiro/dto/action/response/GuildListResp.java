package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>GuildListResp class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class GuildListResp {

    @JsonProperty("guild_id")
    private String guildId;

    @JsonProperty("guild_name")
    private String guildName;

    @JsonProperty("guild_display_id")
    private Long guildDisplayId;

}
