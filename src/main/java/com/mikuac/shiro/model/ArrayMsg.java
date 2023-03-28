package com.mikuac.shiro.model;

import com.mikuac.shiro.enums.MsgTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author Zero
 * @version $Id: $Id
 */
@Data
@Accessors(chain = true)
public class ArrayMsg {

    private MsgTypeEnum type;

    private Map<String, String> data;

}
