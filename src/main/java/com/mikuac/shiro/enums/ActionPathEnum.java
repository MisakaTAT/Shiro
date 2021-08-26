package com.mikuac.shiro.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zero
 */

@Getter
@AllArgsConstructor
public enum ActionPathEnum {
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SEND_PRIVATE_MSG("send_private_msg", "发送私聊消息"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SEND_GROUP_MSG("send_group_msg", "发送群消息"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    DELETE_MSG("delete_msg", "撤回消息"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SET_GROUP_KICK("set_group_kick", "群组踢人"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SET_GROUP_BAN("set_group_ban", "群组单人禁言"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SET_GROUP_WHOLE_BAN("set_group_whole_ban", "群组全体禁言"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SET_GROUP_ADMIN("set_group_admin", "群组设置管理员"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SET_GROUP_ANONYMOUS("set_group_anonymous", "群组匿名"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SET_GROUP_CARD("set_group_card", "设置群名片（群备注）"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SET_GROUP_NAME("set_group_name", "设置群名"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SET_GROUP_LEAVE("set_group_leave", "退出群组"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SET_GROUP_SPECIAL_TITLE("set_group_special_title", "设置群组专属头衔"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SET_FRIEND_ADD_REQUEST("set_friend_add_request", "处理加好友请求"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SET_GROUP_ADD_REQUEST("set_group_add_request", "处理加群请求／邀请"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    GET_LOGIN_INFO("get_login_info", "获取登录号信息"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    GET_STRANGER_INFO("get_stranger_info", "获取陌生人信息"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    GET_FRIEND_LIST("get_friend_list", "获取好友列表"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    DELETE_FRIEND("delete_friend", "删除好友"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    GET_GROUP_INFO("get_group_info", "获取群信息"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    GET_GROUP_LIST("get_group_list", "获取群列表"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    GET_GROUP_MEMBER_INFO("get_group_member_info", "获取群成员信息"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    GET_GROUP_MEMBER_LIST("get_group_member_list", "获取群成员列表"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    GET_GROUP_HONOR_INFO("get_group_honor_info", "获取群荣誉信息"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    CAN_SEND_IMAGE("can_send_image", "检查是否可以发送图片"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    CAN_SEND_RECORD("can_send_record", "检查是否可以发送语音"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SET_GROUP_PORTRAIT("set_group_portrait", "设置群头像"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    CHECK_URL_SAFELY("check_url_safely", "检查链接安全性"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SEN_GROUP_NOTICE("_send_group_notice", "发送群公告"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    GET_GROUP_AT_ALL_REMAIN("get_group_at_all_remain", "获取群 @全体成员 剩余次数"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    UPLOAD_GROUP_FILE("upload_group_file", "上传群文件"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SET_GROUP_ANONYMOUS_BAN("set_group_anonymous_ban", "群组匿名用户禁言"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    DOWNLOAD_FILE("download_file", "下载文件到缓存目录"),
    /**
     * 《关于阿里规范扫描插件及JavaDoc插件非要我加注释这件事》
     */
    SEND_GROUP_FORWARD_MSG("send_group_forward_msg", "发送合并转发 (群)");

    private final String path;
    private final String desc;
}
