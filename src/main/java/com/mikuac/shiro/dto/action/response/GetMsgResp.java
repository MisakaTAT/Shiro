package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created on 2021/9/6.
 *
 * @author Zero
 */
@Data
public class GetMsgResp {

    /**
     * 消息id
     */
    @JSONField(name = "message_id")
    private int messageId;

    /**
     * 消息真实id
     */
    @JSONField(name = "real_id")
    private int realId;

    /**
     * 发送者
     */
    @JSONField(name = "sender")
    private Sender sender;

    /**
     * 发送时间
     */
    @JSONField(name = "time")
    private int time;

    /**
     * 消息内容
     */
    @JSONField(name = "message")
    private String message;

    /**
     * 原始消息内容
     */
    @JSONField(name = "raw_message")
    private String rawMessage;

    /**
     * sender信息
     */
    @Data
    public static class Sender {

        @JSONField(name = "user_id")
        private String userId;

        @JSONField(name = "nickname")
        private String nickname;

        @JSONField(name = "card")
        private String card;

        @JSONField(name = "sex")
        private String sex;

        @JSONField(name = "age")
        private int age;

        @JSONField(name = "area")
        private String area;

        @JSONField(name = "level")
        private String level;

        @JSONField(name = "role")
        private String role;

        @JSONField(name = "title")
        private String title;

    }

}
