package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created on 2021/9/6.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class GetMsgResp {

    /**
     * 消息id
     */
    @JsonProperty("message_id")
    private Integer messageId;

    /**
     * 消息真实id
     */
    @JsonProperty("real_id")
    private Integer realId;

    /**
     * 发送者
     */
    @JsonProperty("sender")
    private Sender sender;

    /**
     * 发送时间
     */
    @JsonProperty("time")
    private Integer time;

    /**
     * 消息内容
     */
    @JsonProperty("message")
    private String message;

    /**
     * 原始消息内容
     */
    @JsonProperty("raw_message")
    private String rawMessage;

    /**
     * 消息类型
     */
    @JsonProperty("message_type")
    private String messageType;

    /**
     * sender信息
     */
    @Data
    public static class Sender {

        @JsonProperty("user_id")
        private String userId;

        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("card")
        private String card;

        @JsonProperty("sex")
        private String sex;

        @JsonProperty("age")
        private Integer age;

        @JsonProperty("area")
        private String area;

        @JsonProperty("level")
        private String level;

        @JsonProperty("role")
        private String role;

        @JsonProperty("title")
        private String title;

    }

}
