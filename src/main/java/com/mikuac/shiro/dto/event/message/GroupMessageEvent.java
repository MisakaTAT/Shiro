package com.mikuac.shiro.dto.event.message;

import com.alibaba.fastjson2.annotation.JSONField;
import com.mikuac.shiro.dto.action.common.Anonymous;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * <p>GroupMessageEvent class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class GroupMessageEvent extends MessageEvent {

    @JSONField(name = "message_id")
    private Integer messageId;

    @JSONField(name = "sub_type")
    private String subType;

    @JSONField(name = "avatar")
    private String avatar;

    @JSONField(name = "real_message_type")
    private String realMessageType;

    @JSONField(name = "is_binded_group_id")
    private Boolean isBindedGroupId;

    @JSONField(name = "group_id")
    private Long groupId;

    @JSONField(name = "anonymous")
    private Anonymous anonymous;

    @JSONField(name = "sender")
    private GroupSender sender;


    @JSONField(name = "is_binded_user_id")
    private Boolean isBindedUserId;

    /**
     * sender信息
     */
    @Data
    public static class GroupSender {

        @JSONField(name = "user_id")
        private Long userId;

        @JSONField(name = "nickname")
        private String nickname;

        @JSONField(name = "card")
        private String card;

        @JSONField(name = "sex")
        private String sex;

        @JSONField(name = "age")
        private Integer age;

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
