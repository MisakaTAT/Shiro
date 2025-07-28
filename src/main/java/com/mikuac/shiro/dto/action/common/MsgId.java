package com.mikuac.shiro.dto.action.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>MsgId class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
public class MsgId {

    @JsonProperty("message_id")
    private Integer messageId;

}
