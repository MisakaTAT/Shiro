package com.mikuac.shiro.dto.action.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>GuildMsgId class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
public class GuildMsgId {

    @JsonProperty("message_id")
    private String messageId;

}
