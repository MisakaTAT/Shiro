package com.mikuac.shiro.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created on 2021/8/10.
 *
 * @author Zero
 */
@Getter
@AllArgsConstructor
public enum ShiroUtilsEnum {

    /**
     * 全体at CQ码
     */
    AT_ALL_CQ_CODE("[CQ:at,qq=all]");

    private final String value;
}
