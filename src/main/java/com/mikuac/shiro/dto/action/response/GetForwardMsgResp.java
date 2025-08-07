package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetForwardMsgResp {

    @JsonProperty("messages")
    private List<MsgResp> messages;

}