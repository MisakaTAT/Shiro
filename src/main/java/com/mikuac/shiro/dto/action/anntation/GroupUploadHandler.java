package com.mikuac.shiro.dto.action.anntation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GroupUploadHandler {


    /**
     * 匹配正则
     */
    String regex() default "none";

    /**
     * 限制某个群
     */
    long[] groupIds() default {};

    /**
     * 排除某个群
     */
    long[] excludeGroupIds() default {};

    /**
     * 是否下载
     */
//    boolean needDownload() default false;

}
