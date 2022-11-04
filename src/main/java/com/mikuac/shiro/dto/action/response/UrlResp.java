package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * <p>UrlResp class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
public class UrlResp {

    @JSONField(name = "url")
    private String url;

}
