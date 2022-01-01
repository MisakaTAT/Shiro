package com.mikuac.shiro.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具类
 *
 * @author Zero
 */
public class RegexUtils {

    /**
     * 消息正则匹配
     *
     * @param regex 正则表达式
     * @param text  匹配内容
     * @return Matcher
     */
    public static Matcher regexMatcher(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.matches()) {
            return matcher;
        } else {
            return null;
        }
    }

}
