package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GroupInfoExResp {

    @JsonProperty("groupCode")
    private long groupCode;// 群号

    @JsonProperty("resultCode")
    private long resultCode;// 结果码

    @JsonProperty("extInfo")
    private ExtInfo extInfo;// 扩展信息

    @Data
    public static class ExtInfo {

        @JsonProperty("groupInfoExtSeq")
        private long groupInfoExtSeq;// 群信息序列号

        @JsonProperty("reserve")
        private int reserve;//?

        @JsonProperty("luckyWordId")
        private int luckyWordId;// 幸运字符ID

        @JsonProperty("lightCharNum")
        private int lightCharNum;//?

        @JsonProperty("luckyWord")
        private String luckyWord;// 幸运字符

        @JsonProperty("starId")
        private long starId;//?

        @JsonProperty("essentialMsgSwitch")
        private int essentialMsgSwitch;// 精华消息开关

        @JsonProperty("todoSeq")
        private long todoSeq;//?

        @JsonProperty("blacklistExpireTime")
        private long blacklistExpireTime;// 黑名单过期时间

        @JsonProperty("isLimitGroupRtc")
        private int isLimitGroupRtc;// 是否限制群视频通话

        @JsonProperty("companyId")
        private long companyId;// 公司ID

        @JsonProperty("hasGroupCustomPortrait")
        private int hasGroupCustomPortrait;// 是否有群自定义头像

        @JsonProperty("bindGuildId")
        private long bindGuildId;// 绑定频道ID？

        @JsonProperty("groupOwnerId")
        private GroupOwner groupOwnerId;// 群主信息

        @JsonProperty("essentialMsgPrivilege")
        private int essentialMsgPrivilege;// 精华消息权限

        @JsonProperty("msgEventSeq")
        private String msgEventSeq;// 消息事件序列号

        @JsonProperty("inviteRobotSwitch")
        private int inviteRobotSwitch;// 邀请机器人开关

        @JsonProperty("gangUpId")
        private String gangUpId;//?

        @JsonProperty("qqMusicMedalSwitch")
        private int qqMusicMedalSwitch;// QQ音乐勋章开关

        @JsonProperty("showPlayTogetherSwitch")
        private int showPlayTogetherSwitch;// 显示一起玩开关

        @JsonProperty("groupFlagPro1")
        private String groupFlagPro1;// 群标识1

    }

    @Data
    public static class GroupOwner {

        @JsonProperty("memberUin")// QQ号
        private long memberUin;

        @JsonProperty("memberUid")// UID
        private String memberUid;

        @JsonProperty("memberQid")// QID
        private String memberQid;
    }

}
