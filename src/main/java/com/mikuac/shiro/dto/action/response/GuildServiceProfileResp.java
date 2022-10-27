package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * <p>GuildServiceProfileResp class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class GuildServiceProfileResp {

    @JSONField(name = "nickname")
    private String nickname;

    @JSONField(name = "tiny_id")
    private String tinyId;

    @JSONField(name = "avatar_url")
    private String avatarUrl;

}
