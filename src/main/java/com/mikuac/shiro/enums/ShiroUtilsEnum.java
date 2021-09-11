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
     * CQ码分割
     */
    CQ_CODE_SPLIT("\\[CQ:"),
    /**
     * 全体at CQ码
     */
    AT_ALL_CQ_CODE("[CQ:at,qq=all]"),
    /**
     * 链接提取正则
     */
    GET_URL_REGEX("(http|https)://[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])?"),
    /**
     * at对象提取正则
     */
    GET_AT_USER_ID_REGEX("at,qq=(.*)\\]");

    private final String value;
}
