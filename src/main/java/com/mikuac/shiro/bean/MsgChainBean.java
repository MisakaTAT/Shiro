package com.mikuac.shiro.bean;

import lombok.Data;

import java.util.Map;

/**
 * @author Zero
 */
@Data
public class MsgChainBean {

    private String type;
    
    private Map<String, String> data;

}
