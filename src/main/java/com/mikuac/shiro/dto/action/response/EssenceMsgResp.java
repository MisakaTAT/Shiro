package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author Zero
 */
@Data
public class EssenceMsgResp {

    @JSONField(name = "sender_id")
    private long senderId;

    @JSONField(name = "sender_nick")
    private String senderNick;

    @JSONField(name = "sender_time")
    private long senderTime;

    @JSONField(name = "operator_id")
    private long operatorId;

    @JSONField(name = "operator_nick")
    private String operatorNick;

    @JSONField(name = "operator_time")
    private String operatorTime;

    @JSONField(name = "message_id")
    private String messageId;

}
