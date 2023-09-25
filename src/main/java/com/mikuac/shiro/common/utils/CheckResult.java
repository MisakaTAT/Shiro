package com.mikuac.shiro.common.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.regex.Matcher;

/**
 * 存储 Filter 校验结果的类
 */
@Getter
@Setter
@Accessors(chain = true)
public class CheckResult {
    private boolean result;
    private Matcher matcher;

    /**
     * 默认创建 false 的结果
     */
    public CheckResult(){
        result = false;
        matcher = null;
    }
    public void changeResult() {
        result = !result;
    }
}
