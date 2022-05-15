package com.mikuac.shiro.dto.action.common;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * @author zero
 */
@Data
public class MsgId {

    @JSONField(name = "message_id")
    private int messageId;

}
