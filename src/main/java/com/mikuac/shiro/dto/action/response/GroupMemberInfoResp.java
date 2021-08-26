package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created on 2021/7/8.
 *
 * @author Zero
 */
@Data
public class GroupMemberInfoResp {

    @JSONField(name = "group_id")
    private long groupId;

    @JSONField(name = "user_id")
    private long userId;

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

    @JSONField(name = "join_time")
    private int joinTime;

    @JSONField(name = "last_sent_time")
    private int lastSentTime;

    @JSONField(name = "level")
    private String level;

    @JSONField(name = "role")
    private String role;

    @JSONField(name = "unfriendly")
    private boolean unfriendly;

    @JSONField(name = "title")
    private String title;

    @JSONField(name = "title_expire_time")
    private long titleExpireTime;

    @JSONField(name = "card_changeable")
    private boolean cardChangeable;

}
