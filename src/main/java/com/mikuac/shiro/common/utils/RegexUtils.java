package com.mikuac.shiro.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具类
 *
 * @author Zero
 * @version $Id: $Id
 */
public class RegexUtils {

    private RegexUtils() {
    }

    private static final Map<String, Pattern> cache = new HashMap<>();

    /**
     * 正则匹配
     *
     * @param regex 正则表达式
     * @param text  匹配内容
     * @return {@link Optional} of {@link Matcher}
     */
    public static Optional<Matcher> matcher(String regex, String text) {
        Pattern pattern = cache.computeIfAbsent(regex, Pattern::compile);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches() ? Optional.of(matcher) : Optional.empty();
    }

}
