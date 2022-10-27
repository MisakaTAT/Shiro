package com.mikuac.shiro.dto.action.common;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * <p>ActionData class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
public class ActionData<T> {

    @JSONField(name = "status")
    private String status;

    @JSONField(name = "retcode")
    private int retCode;

    @JSONField(name = "data")
    private T data;

}
