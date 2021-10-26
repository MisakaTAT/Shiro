package com.mikuac.shiro.common.anntation;


import com.mikuac.shiro.enums.ContainsPictureEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PrivateMessageHandler {

    /**
     * 匹配正则
     */
    String regex() default "none";

    /**
     * 限制发言人
     */
    long[] senderIds() default {};

    long[] excludeSenderIds() default {};


    //是否包含图片
    ContainsPictureEnum isContainsPicture () default ContainsPictureEnum.OFF;


}
