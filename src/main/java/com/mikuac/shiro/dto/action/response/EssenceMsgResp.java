package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * <p>EssenceMsgResp class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class EssenceMsgResp {

    @JSONField(name = "sender_id")
    private Long senderId;

    @JSONField(name = "sender_nick")
    private String senderNick;

    @JSONField(name = "sender_time")
    private Long senderTime;

    @JSONField(name = "operator_id")
    private Long operatorId;

    @JSONField(name = "operator_nick")
    private String operatorNick;

    @JSONField(name = "operator_time")
    private String operatorTime;

    @JSONField(name = "message_id")
    private Integer messageId;

}
