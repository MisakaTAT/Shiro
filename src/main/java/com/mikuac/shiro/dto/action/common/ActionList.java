package com.mikuac.shiro.dto.action.common;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * Created on 2021/7/8.
 *
 * @author Zero
 */
@Data
public class ActionList<T> {

    @JSONField(name = "status")
    private String status;

    @JSONField(name = "retcode")
    private int retcode;

    @JSONField(name = "data")
    private List<T> data;

}
