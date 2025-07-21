package com.mikuac.shiro.dto.action.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * <p>Anonymous class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
public class Anonymous {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("flag")
    private String flag;

}
