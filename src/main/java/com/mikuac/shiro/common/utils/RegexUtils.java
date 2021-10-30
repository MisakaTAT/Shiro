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
     * @param regex 正则表达式
     * @param text  匹配内容
     * @return 返回匹配内容
     */
    public static String regex(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }


    public static Matcher regexMacher(String regex, String text) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.lookingAt()) {
            return matcher;
        } else {
            return null;
        }
    }

    /**
     * 取正则分组匹配内容
     *
     * @param regex   正则表达式
     * @param text    匹配内容
     * @param groupId 组ID
     * @return 匹配内容
     */
    public static String regexGroup(String regex, String text, int groupId) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(groupId);
        }else {
        return null;
    }}

}
