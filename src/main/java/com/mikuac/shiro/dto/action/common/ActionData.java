package com.mikuac.shiro.dto.action.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>ActionData class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
public class ActionData<T> {

    @JsonProperty("status")
    private String status;

    @JsonProperty("retcode")
    private Integer retCode;

    @JsonProperty("data")
    private T data;

}
