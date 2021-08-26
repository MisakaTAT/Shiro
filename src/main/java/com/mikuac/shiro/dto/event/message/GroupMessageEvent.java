package com.mikuac.shiro.dto.event.message;

import com.alibaba.fastjson.annotation.JSONField;
import com.mikuac.shiro.dto.action.common.Anonymous;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author zero
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class GroupMessageEvent extends MessageEvent {

    @JSONField(name = "sub_type")
    private String subType;

    @JSONField(name = "group_id")
    private long groupId;

    @JSONField(name = "anonymous")
    private Anonymous anonymous;

    @JSONField(name = "sender")
    private GroupSender sender;

    /**
     * sender信息
     */
    @Data
    public static class GroupSender {

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
