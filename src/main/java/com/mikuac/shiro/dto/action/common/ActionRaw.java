package com.mikuac.shiro.dto.action.common;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created on 2021/7/8.
 *
 * @author Zero
 */
@Data
public class ActionRaw {

    @JSONField(name = "status")
    private String status;

    @JSONField(name = "retcode")
    private int retCode;

    @JSONField(name = "echo")
    private long echo;

}
