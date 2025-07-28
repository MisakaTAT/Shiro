package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>GuildMemberListResp class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class GuildMemberListResp {

    /**
     * 成员列表
     */
    @JsonProperty("members")
    private List<GuildMemberInfo> members;

    /**
     * 是否最终页
     */
    @JsonProperty("finished")
    private Boolean finished;

    /**
     * 翻页Token
     */
    @JsonProperty("next_token")
    private String nextToken;

    @Data
    private static class GuildMemberInfo {

        /**
         * 成员ID
         */
        @JsonProperty("tiny_id")
        private String tinyId;

        /**
         * 成员昵称
         */
        @JsonProperty("nickname")
        private String nickname;

        /**
         * 成员头衔
         */
        @JsonProperty("title")
        private String title;

        /**
         * 所在权限组ID
         * 默认情况下频道管理员的权限组ID为 2, 部分频道可能会另行创建, 需手动判断
         * 此接口仅展现最新的权限组, 获取用户加入的所有权限组请使用 get_guild_member_profile 接口
         */
        @JsonProperty("role_id")
        private String roleId;

        /**
         * 所在权限组名称
         */
        @JsonProperty("role_name")
        private String roleName;

    }

}
