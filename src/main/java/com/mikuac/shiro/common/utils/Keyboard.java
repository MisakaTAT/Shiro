package com.mikuac.shiro.common.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义按钮内容，一般可以发送5行按钮，每一行一般5个按钮。
 */
@Getter
@Setter
@SuppressWarnings("unused")
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
    private String type = "keyboard";

    private Data data = new Data();

    public static Keyboard builder() {
        return new Keyboard();
    }

    public static ButtonBuilder callButtonBuilder() {
        return new ButtonBuilder().actionType(ACTION_TYPE_CALLBACK);
    }

    public static ButtonBuilder textButtonBuilder() {
        return new ButtonBuilder().actionType(ACTION_TYPE_CMD);
    }

    public static ButtonBuilder urlButtonBuilder() {
        return new ButtonBuilder().actionType(ACTION_TYPE_JUMP);
    }

    public Keyboard build() {
        return this;
    }

    /**
     * 添加按钮
     *
     * @param button {@code Keyboard.TextButtonBuilder().build()}
     * @return {@link Keyboard}
     */
    public Keyboard addButton(Button button) {
        List<Row> rows = getData().getContent().getRows();
        if (rows.isEmpty()) {
            addRow();
        }
        rows.get(rows.size() - 1).addButton(button);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Keyboard addRow() {
        List<Row> rows = getData().getContent().getRows();
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

        public void addButton(Button button) {
            getButtons().add(button);
        }
    }

    @Getter
    @Setter
    public static class Button {
        /**
         * 按钮ID：在一个keyboard消息内设置唯一
         */
        @JsonProperty("id")
        private String id;

        @JsonProperty("render_data")
        private RenderData renderData = new RenderData();

        @JsonProperty("action")
        private Action action = new Action();
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class RenderData {
        /**
         * 按钮上的文字
         */
        @JsonProperty("label")
        private String label;

        /**
         * 点击后按钮的上文字
         */
        @JsonProperty("visited_label")
        private String visitedLabel;

        /**
         * 按钮样式：0 灰色线框，1 蓝色线框
         */
        @JsonProperty("style")
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
        @JsonProperty("type")
        private int type;

        /**
         * 权限设置
         */
        @JsonProperty("permission")
        private Permission permission = new Permission();

        /**
         * 操作相关的数据
         */
        @JsonProperty("data")
        private String data = "";

        /**
         * 指令按钮可用，指令是否带引用回复本消息，默认 false。支持版本 8983
         */
        @JsonProperty("reply")
        private Boolean reply;

        /**
         * 指令按钮可用，点击按钮后直接自动发送 data，默认 false。支持版本 8983
         */
        @JsonProperty("enter")
        private Boolean enter;

        /**
         * 本字段仅在指令按钮下有效，设置后后会忽略 action.enter 配置。
         * 设置为 1 时 ，点击按钮自动唤起启手Q选图器，其他值暂无效果。
         * 仅支持手机端版本 8983+ 的单聊场景，桌面端不支持）
         */
        @JsonProperty("anchor")
        private Integer anchor;

        /**
         * 客户端不支持本action的时候，弹出的toast文案
         */
        @JsonProperty("unsupport_tips")
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
        @JsonProperty("type")
        private Integer type;

        /**
         * 有权限的用户 id 的列表
         */
        @JsonProperty("specify_user_ids")
        private List<String> specifyUserIds;

        /**
         * 有权限的身份组 id 的列表（仅频道可用）
         */
        @JsonProperty("specify_role_ids")
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
        private String label = "按钮";

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
         * 有权限的用户 id 的列表
         */
        private List<String> specifyUserIds;

        /**
         * 有权限的身份组 id 的列表（仅频道可用）
         */
        private List<String> specifyRoleIds;

        /**
         * 本字段仅在指令按钮下有效，设置后后会忽略 action.enter 配置。
         * 设置为 {@link Keyboard#ANCHOR_SELECT} 时 ，点击按钮自动唤起启手Q选图器，其他值暂无效果。
         * 仅支持手机端版本 8983+ 的单聊场景，桌面端不支持）
         */
        private Integer anchor = ANCHOR_NONE;

        /**
         * 客户端不支持本action的时候，弹出的toast文案
         */
        private String unSupportTips = "暂不支持当前版本";

        public Button build() {
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
            button.getAction().getPermission()
                    .setType(permissionType)
                    .setSpecifyUserIds(specifyUserIds)
                    .setSpecifyRoleIds(specifyRoleIds);
            return button;
        }
    }

}
