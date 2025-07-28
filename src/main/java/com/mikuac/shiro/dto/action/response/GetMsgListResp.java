package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetMsgListResp {

    @JsonProperty("status")
    private String status;

    @JsonProperty("retcode")
    private int retcode;

    @JsonProperty("messages")
    private List<GetMsgResp> messages;

}
