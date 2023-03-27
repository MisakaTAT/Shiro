package com.mikuac.shiro.bo;

import com.mikuac.shiro.enums.MsgTypeEnum;
import lombok.Data;

import java.util.Map;

/**
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class ArrayMsg {

    private MsgTypeEnum type;

    private Map<String, String> data;

}
