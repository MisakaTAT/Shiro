package com.mikuac.shiro.dto.action.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Created on 2021/7/8.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class ActionList<T> {

    @JsonProperty("status")
    private String status;

    @JsonProperty("retcode")
    private Integer retCode;

    @JsonProperty("data")
    private List<T> data;

}
