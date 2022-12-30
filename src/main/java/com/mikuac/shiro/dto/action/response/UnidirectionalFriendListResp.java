package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * <p>UnidirectionalFriendListResp class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
public class UnidirectionalFriendListResp {

    @JSONField(name = "user_id")
    private Long userId;

    @JSONField(name = "nickname")
    private String nickname;

    @JSONField(name = "source")
    private String source;

}
