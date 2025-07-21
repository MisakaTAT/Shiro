package com.mikuac.shiro.dto.event.message;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("message_id")
    private Integer messageId;

    @JsonProperty("sub_type")
    private String subType;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("real_message_type")
    private String realMessageType;

    @JsonProperty("is_binded_group_id")
    private Boolean isBindedGroupId;

    @JsonProperty("group_id")
    private Long groupId;

    @JsonProperty("anonymous")
    private Anonymous anonymous;

    @JsonProperty("sender")
    private GroupSender sender;

    @JsonProperty("is_binded_user_id")
    private Boolean isBindedUserId;

    /**
     * sender信息
     */
    @Data
    public static class GroupSender {

        @JsonProperty("user_id")
        private Long userId;

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
