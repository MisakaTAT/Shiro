package com.mikuac.shiro.dto.action.common;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author zero
 */
@Data
public class ActionData<T> {

    @JSONField(name = "status")
    private String status;

    @JSONField(name = "retcode")
    private int retcode;

    @JSONField(name = "data")
    private T data;

}
