package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>WordSlicesResp class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
public class WordSlicesResp {

    @JsonProperty("slices")
    private List<String> slices;

}
