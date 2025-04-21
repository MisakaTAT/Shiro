package com.mikuac.shiro.dto.action.response;

import lombok.Data;

import java.util.List;

@Data
public class GetMsgListResp {
    private String status;
    private int retcode;
    private List<GetMsgResp> messages;
}
