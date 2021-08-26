package com.mikuac.shiro.dto.action.common;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author zero
 */
@Data
public class Anonymous {

    @JSONField(name = "id")
    private long id;

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "flag")
    private String flag;

}
