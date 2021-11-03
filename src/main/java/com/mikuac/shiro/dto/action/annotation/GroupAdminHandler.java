package com.mikuac.shiro.dto.action.annotation;


import com.mikuac.shiro.enums.AdminNoticeTypeEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GroupAdminHandler {

    AdminNoticeTypeEnum TYPE_ENUM()default AdminNoticeTypeEnum.ON;

    /**
     * 限制某个群
     */
    long[] groupIds() default {};

    /**
     * 排除某个群
     */
    long[] excludeGroupIds() default {};

}
