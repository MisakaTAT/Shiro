package com.mikuac.shiro.dto.event.message;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 私聊消息
 *
 * @author zero
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class PrivateMessageEvent extends MessageEvent {

    @JSONField(name = "sub_type")
    private String subType;

    @JSONField(name = "sender")
    private PrivateSender privateSender;

    /**
     * sender信息
     */
    @Data
    public static class PrivateSender {

        @JSONField(name = "user_id")
        private long userId;

        @JSONField(name = "nickname")
        private String nickname;

        @JSONField(name = "sex")
        private String sex;

        @JSONField(name = "age")
        private int age;

    }

}
