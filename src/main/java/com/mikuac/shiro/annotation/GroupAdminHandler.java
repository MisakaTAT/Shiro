package com.mikuac.shiro.annotation;


import com.mikuac.shiro.enums.AdminNoticeTypeEnum;

import java.lang.annotation.*;

/**
 * 群管理员变动事件注解
 *
 * @author meme
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GroupAdminHandler {

    /**
     * 通知类型
     * 默认为 ALL 即设置管理员与取消管理员都会通知
     * 若值为 SET 仅通知设置管理员
     * 若值为 UNSET 仅通知取消管理员
     *
     * @return 通知类型
     */
    AdminNoticeTypeEnum type() default AdminNoticeTypeEnum.ALL;

    /**
     * 群组白名单（仅响应白名单内群组的通知）
     *
     * @return 群白名单数组
     */
    long[] groupWhiteList() default {};

    /**
     * 群组黑名单（仅响应不在黑名单内群组的通知）
     *
     * @return 群黑名单数组
     */
    long[] groupBlackList() default {};

}
