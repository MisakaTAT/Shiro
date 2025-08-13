package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created on 2021/9/6.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MsgResp extends MessageEvent {

    @JsonProperty("sender")
    private Sender sender;

    @Data
    public static class Sender {

        @JsonProperty("user_id")
        private String userId;

        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("card")
        private String card;

    }

}
