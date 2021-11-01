package com.mikuac.shiro.dto.action.anntation;


import com.mikuac.shiro.enums.ContainsPictureEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PrivateMessageHandler {

    String regex() default "none";


    long[] senderIds() default {};

    long[] excludeSenderIds() default {};


    //是否包含图片
    ContainsPictureEnum isContainsPicture () default ContainsPictureEnum.OFF;


}
