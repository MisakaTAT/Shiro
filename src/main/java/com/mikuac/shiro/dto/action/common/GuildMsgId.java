package com.mikuac.shiro.dto.action.common;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * <p>GuildMsgId class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
public class GuildMsgId {

    @JSONField(name = "message_id")
    private String messageId;

}
