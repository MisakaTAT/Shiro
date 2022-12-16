package com.mikuac.shiro.bo;

import lombok.Data;

import java.util.Map;

/**
 * <p>MsgChainBean class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class ArrayMsg {

    private String type;

    private Map<String, String> data;

}
