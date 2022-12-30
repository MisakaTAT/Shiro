package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * Created on 2021/7/8.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class LoginInfoResp {

    @JSONField(name = "user_id")
    private Long userId;

    @JSONField(name = "nickname")
    private String nickname;

}
