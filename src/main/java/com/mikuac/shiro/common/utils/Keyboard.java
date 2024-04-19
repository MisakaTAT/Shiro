package com.mikuac.shiro.common.utils;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义按钮内容，最多可以发送5行按钮，每一行最多5个按钮。
 */
@Getter
@Setter
public class Keyboard {
    public static final int STYLE_GREY = 0;
    public static final int STYLE_BLUE = 1;
    public static final int ACTION_TYPE_JUMP = 0;
    public static final int ACTION_TYPE_CALLBACK = 1;
    public static final int ACTION_TYPE_CMD = 2;
    public static final int PERMISSION_TYPE_USER = 0;
    public static final int PERMISSION_TYPE_MANAGER = 1;
    public static final int PERMISSION_TYPE_ALL = 2;
    public static final int PERMISSION_TYPE_ROLE = 3;
    public static final int ANCHOR_NONE = 0;
    public static final int ANCHOR_SELECT = 1;

    private static final int MAX_BUTTON_NUM = 5;
    private static final int MAX_ROW_NUM = 5;

    private final String type = "keyboard";
    private Data data = new Data();

    public static Keyboard Builder() {
        return new Keyboard();
    }

    public static ButtonBuilder CallButtonBuilder() {
        return new ButtonBuilder().actionType(ACTION_TYPE_CALLBACK);
    }

    public static ButtonBuilder TextButtonBuilder() {
        return new ButtonBuilder().actionType(ACTION_TYPE_CMD);
    }

    public static ButtonBuilder UrlButtonBuilder() {
        return new ButtonBuilder().actionType(ACTION_TYPE_JUMP);
    }

    public Keyboard addButton(ButtonBuilder builder) {
        List<Long> specifyUserIds = null;
        List<Long> specifyRoleIds = null;
        if (builder.permissionType() == PERMISSION_TYPE_USER) {
            specifyUserIds = builder.specifies();
        } else if (builder.permissionType() == PERMISSION_TYPE_ROLE) {
            specifyRoleIds = builder.specifies();
        }
        if (builder.visitedLabel() == null) {
            builder.visitedLabel(builder.text());
        }
        return addButton(builder.id(), builder.text(), builder.visitedLabel(), builder.style(), builder.actionType(), builder.data(), builder.reply(), builder.enter(), builder.anchor(), builder.unSupportTips(), builder.permissionType(), specifyUserIds, specifyRoleIds);
    }

    /**
     * 添加按钮
     *
     * @param label          按钮上的文字
     * @param visitedLabel   点击后按钮的上文字
     * @param style          按钮样式:<br/>
     *                       {@link Keyboard#STYLE_GREY} 灰色线框<br/>
     *                       {@link Keyboard#STYLE_BLUE} 蓝色线框
     * @param actionType     按钮类型:<br/>
     *                       {@link Keyboard#ACTION_TYPE_JUMP} 跳转按钮: http 或 小程序 客户端识别 scheme<br/>
     *                       {@link Keyboard#ACTION_TYPE_CALLBACK} 回调按钮: 回调后台接口, data 传给后台<br/>
     *                       {@link Keyboard#ACTION_TYPE_CMD} 指令按钮: 自动在输入框插入 @bot data<br/>
     * @param data           操作相关的数据
     * @param reply          指令按钮可用，指令是否带引用回复本消息，默认 false。支持版本 8983
     * @param enter          指令按钮可用，点击按钮后直接自动发送 data，默认 false。支持版本 8983
     * @param permissionType 按钮权限类型:<br/>
     *                       {@link Keyboard#PERMISSION_TYPE_USER} 指定用户可操作<br/>
     *                       {@link Keyboard#PERMISSION_TYPE_MANAGER} 仅管理者可操作<br/>
     *                       {@link Keyboard#PERMISSION_TYPE_ALL} 所有人可操作<br/>
     *                       {@link Keyboard#PERMISSION_TYPE_ROLE} 指定身份组可操作（仅频道可用）<br/>
     * @param specifyUserIds 有权限的用户 id 的列表
     * @param specifyRoleIds 有权限的身份组 id 的列表（仅频道可用）
     * @return {@link Keyboard}
     */
    public Keyboard addButton(String id, String label, String visitedLabel, Integer style, Integer actionType, String data, Boolean reply, Boolean enter, Integer anchor, String unSupportTips, Integer permissionType, List<Long> specifyUserIds, List<Long> specifyRoleIds) {
        Button button = new Button();
        button.setId(id);
        button.getRenderData()
                .setLabel(label)
                .setVisitedLabel(visitedLabel)
                .setStyle(style);
        button.getAction()
                .setType(actionType)
                .setData(data)
                .setReply(reply)
                .setEnter(enter)
                .setAnchor(anchor)
                .setUnSupportTips(unSupportTips);
        Permission permission = button.getAction().getPermission()
                .setType(permissionType);
        if (specifyUserIds != null && !specifyUserIds.isEmpty()) {
            List<String> list = specifyUserIds.stream().map(String::valueOf).toList();
            permission.setSpecifyUserIds(list);
        }
        if (specifyRoleIds != null && !specifyRoleIds.isEmpty()) {
            List<String> list = specifyRoleIds.stream().map(String::valueOf).toList();
            permission.setSpecifyRoleIds(list);
        }
        List<Row> rows = getData().getContent().getRows();
        if (rows.isEmpty()) {
            addRow();
        }
        if (!rows.get(rows.size() - 1).addButton(button)) {
            addRow();
            rows.get(rows.size() - 1).addButton(button);
        }
        return this;
    }

    public Keyboard addRow() {
        List<Row> rows = getData().getContent().getRows();
        if (rows.size() >= MAX_ROW_NUM) {
            return this;
        }
        if (!rows.isEmpty() && rows.get(rows.size() - 1).getButtons().isEmpty()) {
            return this;
        }
        rows.add(new Row());
        return this;
    }

    @Getter
    @Setter
    public static class Data {
        private Content content = new Content();
    }

    @Getter
    @Setter
    public static class Content {
        private List<Row> rows = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class Row {
        private List<Button> buttons = new ArrayList<>();

        public boolean addButton(Button button) {
            if (getButtons().size() >= MAX_BUTTON_NUM) {
                return false;
            }
            getButtons().add(button);
            return true;
        }
    }

    @Getter
    @Setter
    public static class Button {
        /**
         * 按钮ID：在一个keyboard消息内设置唯一
         */
        @JSONField(name = "id")
        private String id;
        @JSONField(name = "render_data")
        private RenderData renderData = new RenderData();
        @JSONField(name = "action")
        private Action action = new Action();
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class RenderData {
        /**
         * 按钮上的文字
         */
        @JSONField(name = "label")
        private String label;
        /**
         * 点击后按钮的上文字
         */
        @JSONField(name = "visited_label")
        private String visitedLabel;
        /**
         * 按钮样式：0 灰色线框，1 蓝色线框
         */
        @JSONField(name = "style")
        private Integer style;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Action {
        /**
         * 设置 0 跳转按钮：http 或 小程序 客户端识别 scheme，<br/>
         * 设置 1 回调按钮：回调后台接口, data 传给后台，<br/>
         * 设置 2 指令按钮：自动在输入框插入 @bot data <br/>
         */
        @JSONField(name = "type")
        private int type;
        /**
         * 权限设置
         */
        @JSONField(name = "permission")
        private Permission permission = new Permission();
        /**
         * 操作相关的数据
         */
        @JSONField(name = "data")
        private String data = "";
        /**
         * 指令按钮可用，指令是否带引用回复本消息，默认 false。支持版本 8983
         */
        @JSONField(name = "reply")
        private Boolean reply;
        /**
         * 指令按钮可用，点击按钮后直接自动发送 data，默认 false。支持版本 8983
         */
        @JSONField(name = "enter")
        private Boolean enter;
        /**
         * 本字段仅在指令按钮下有效，设置后后会忽略 action.enter 配置。
         * 设置为 1 时 ，点击按钮自动唤起启手Q选图器，其他值暂无效果。
         * 仅支持手机端版本 8983+ 的单聊场景，桌面端不支持）
         */
        @JSONField(name = "anchor")
        private Integer anchor;
        /**
         * 客户端不支持本action的时候，弹出的toast文案
         */
        @JSONField(name = "unsupport_tips")
        private String unSupportTips;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Permission {
        /**
         * 0 指定用户可操作，<br/>
         * 1 仅管理者可操作，<br/>
         * 2 所有人可操作，<br/>
         * 3 指定身份组可操作（仅频道可用）<br/>
         */
        @JSONField(name = "type")
        private Integer type;
        /**
         * 有权限的用户 id 的列表
         */
        @JSONField(name = "specify_user_ids")
        private List<String> specifyUserIds;
        /**
         * 有权限的身份组 id 的列表（仅频道可用）
         */
        @JSONField(name = "specify_role_ids")
        private List<String> specifyRoleIds;
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    public static class ButtonBuilder {
        /**
         * 按钮ID：在一个keyboard消息内设置唯一
         */
        private String id;
        /**
         * 按钮上的文字
         */
        private String text = "按钮";
        /**
         * 点击后按钮的上文字
         */
        private String visitedLabel;
        /**
         * 按钮样式:<br/>
         * {@link Keyboard#STYLE_GREY} 灰色线框<br/>
         * {@link Keyboard#STYLE_BLUE} 蓝色线框
         */
        private Integer style = STYLE_BLUE;
        /**
         * 按钮类型:<br/>
         * {@link Keyboard#ACTION_TYPE_JUMP} 跳转按钮: http 或 小程序 客户端识别 scheme<br/>
         * {@link Keyboard#ACTION_TYPE_CALLBACK} 回调按钮: 回调后台接口, data 传给后台<br/>
         * {@link Keyboard#ACTION_TYPE_CMD} 指令按钮: 自动在输入框插入 @bot data<br/>
         */
        private Integer actionType = ACTION_TYPE_CMD;
        /**
         * 操作相关的数据
         */
        private String data = "";
        /**
         * 指令按钮可用，指令是否带引用回复本消息，默认 false。支持版本 8983
         */
        private Boolean reply = false;
        /**
         * 指令按钮可用，点击按钮后直接自动发送 data，默认 false。支持版本 8983
         */
        private Boolean enter = false;
        /**
         * 按钮权限类型:<br/>
         * {@link Keyboard#PERMISSION_TYPE_USER} 指定用户可操作<br/>
         * {@link Keyboard#PERMISSION_TYPE_MANAGER} 仅管理者可操作<br/>
         * {@link Keyboard#PERMISSION_TYPE_ALL} 所有人可操作<br/>
         * {@link Keyboard#PERMISSION_TYPE_ROLE} 指定身份组可操作（仅频道可用）<br/>
         */
        private int permissionType = PERMISSION_TYPE_ALL;
        /**
         * 有权限的 id 的列表
         */
        private List<Long> specifies;
        /**
         * 本字段仅在指令按钮下有效，设置后后会忽略 action.enter 配置。
         * 设置为 1 时 ，点击按钮自动唤起启手Q选图器，其他值暂无效果。
         * 仅支持手机端版本 8983+ 的单聊场景，桌面端不支持）
         */
        private Integer anchor = ANCHOR_NONE;
        /**
         * 客户端不支持本action的时候，弹出的toast文案
         */
        private String unSupportTips = "暂不支持当前版本";
    }

}
