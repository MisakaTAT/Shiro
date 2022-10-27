package com.mikuac.shiro.bean;

import lombok.Data;

import java.util.Map;

/**
 * <p>MsgChainBean class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class MsgChainBean {

    private String type;

    private Map<String, String> data;

}
