package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class GroupInfoExResp {

    @JSONField(name = "groupCode")
    private long groupCode;// 群号

    @JSONField(name = "resultCode")
    private long resultCode;// 结果码

    @JSONField(name = "extInfo")
    private ExtInfo extInfo;// 扩展信息

    @Data
    public static class ExtInfo {

        @JSONField(name = "groupInfoExtSeq")
        private long groupInfoExtSeq;// 群信息序列号

        @JSONField(name = "reserve")
        private int reserve;//?

        @JSONField(name = "luckyWordId")
        private int luckyWordId;// 幸运字符ID

        @JSONField(name = "lightCharNum")
        private int lightCharNum;//?

        @JSONField(name = "luckyWord")
        private String luckyWord;// 幸运字符

        @JSONField(name = "starId")
        private long starId;//?

        @JSONField(name = "essentialMsgSwitch")
        private int essentialMsgSwitch;// 精华消息开关

        @JSONField(name = "todoSeq")
        private long todoSeq;//?

        @JSONField(name = "blacklistExpireTime")
        private long blacklistExpireTime;// 黑名单过期时间

        @JSONField(name = "isLimitGroupRtc")
        private int isLimitGroupRtc;// 是否限制群视频通话

        @JSONField(name = "companyId")
        private long companyId;// 公司ID

        @JSONField(name = "hasGroupCustomPortrait")
        private int hasGroupCustomPortrait;// 是否有群自定义头像

        @JSONField(name = "bindGuildId")
        private long bindGuildId;// 绑定频道ID？

        @JSONField(name = "groupOwnerId")
        private GroupOwner groupOwnerId;// 群主信息

        @JSONField(name = "essentialMsgPrivilege")
        private int essentialMsgPrivilege;// 精华消息权限

        @JSONField(name = "msgEventSeq")
        private String msgEventSeq;// 消息事件序列号

        @JSONField(name = "inviteRobotSwitch")
        private int inviteRobotSwitch;// 邀请机器人开关

        @JSONField(name = "gangUpId")
        private String gangUpId;//?

        @JSONField(name = "qqMusicMedalSwitch")
        private int qqMusicMedalSwitch;// QQ音乐勋章开关

        @JSONField(name = "showPlayTogetherSwitch")
        private int showPlayTogetherSwitch;// 显示一起玩开关

        @JSONField(name = "groupFlagPro1")
        private String groupFlagPro1;// 群标识1

    }

    @Data
    public static class GroupOwner {

        @JSONField(name = "memberUin")// QQ号
        private long memberUin;

        @JSONField(name = "memberUid")// UID
        private String memberUid;

        @JSONField(name = "memberQid")// QID
        private String memberQid;
    }

}
