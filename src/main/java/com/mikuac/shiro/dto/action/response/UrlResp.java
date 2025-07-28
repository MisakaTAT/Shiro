package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>UrlResp class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
public class UrlResp {

    @JsonProperty("url")
    private String url;

}
