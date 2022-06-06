package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author zero
 */
@Data
public class WordSlicesResp {

    @JSONField(name = "slices")
    private List<String> slices;

}
