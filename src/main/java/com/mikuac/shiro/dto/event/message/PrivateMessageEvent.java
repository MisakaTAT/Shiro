package com.mikuac.shiro.dto.event.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 私聊消息
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class PrivateMessageEvent extends MessageEvent {

    /**
     * 消息 ID
     */
    @JsonProperty("message_id")
    private Integer messageId;

    /**
     * 消息子类型, 如果是好友则是 friend, 如果是群临时会话则是 group, 如果是在群中自身发送则是 group_self
     */
    @JsonProperty("sub_type")
    private String subType;

    /**
     * 发送人信息
     */
    @JsonProperty("sender")
    private PrivateSender privateSender;

    /**
     * 临时会话来源
     */
    @JsonProperty("temp_source")
    private Integer tempSource;

    /**
     * sender信息
     */
    @Data
    public static class PrivateSender {

        /**
         * 当 subType 值为 group 时，表示本次私聊事件为临时会话，此时 groupId 为来源 QQ 群
         */
        @JsonProperty("group_id")
        private Long groupId;

        @JsonProperty("user_id")
        private Long userId;

        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("sex")
        private String sex;

        @JsonProperty("age")
        private Integer age;

    }

}
