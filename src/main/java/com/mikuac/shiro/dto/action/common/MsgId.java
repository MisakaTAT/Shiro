package com.mikuac.shiro.dto.action.common;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * <p>MsgId class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
public class MsgId {

    @JSONField(name = "message_id")
    private Integer messageId;

}
