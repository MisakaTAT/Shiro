package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
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

    @JSONField(name = "slices")
    private List<String> slices;

}
