package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>EssenceMsgResp class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class EssenceMsgResp {

    @JsonProperty("sender_id")
    private Long senderId;

    @JsonProperty("sender_nick")
    private String senderNick;

    @JsonProperty("sender_time")
    private Long senderTime;

    @JsonProperty("operator_id")
    private Long operatorId;

    @JsonProperty("operator_nick")
    private String operatorNick;

    @JsonProperty("operator_time")
    private String operatorTime;

    @JsonProperty("message_id")
    private Integer messageId;

}
