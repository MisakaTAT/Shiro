package com.mikuac.shiro.dto.action.common;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * <p>Anonymous class.</p>
 *
 * @author zero
 * @version $Id: $Id
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
