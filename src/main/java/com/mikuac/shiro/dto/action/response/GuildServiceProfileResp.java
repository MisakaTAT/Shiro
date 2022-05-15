package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author Zero
 */
@Data
public class GuildServiceProfileResp {

    @JSONField(name = "nickname")
    String nickname;

    @JSONField(name = "tiny_id")
    String tinyId;

    @JSONField(name = "avatar_url")
    String avatarUrl;

}
