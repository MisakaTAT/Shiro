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
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    CQ_CODE_SPLIT("\\[CQ:", "CQ码分割"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    AT_ALL_CQ_CODE("[CQ:at,qq=all]", "全体at CQ码"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    GET_URL_REGEX("(http|https)://[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-.,@?^=%&:/~+#]*[\\w\\-@?^=%&/~+#])?", "链接提取正则"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    GET_AT_USER_ID_REGEX("at,qq=(.*)\\]", "at对象提取正则");
    private final String value;
    private final String desc;
}
