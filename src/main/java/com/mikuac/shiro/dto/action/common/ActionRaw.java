package com.mikuac.shiro.dto.action.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created on 2021/7/8.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class ActionRaw {

    @JsonProperty("status")
    private String status;

    @JsonProperty("retcode")
    private Integer retCode;

    @JsonProperty("echo")
    private Long echo;

}
