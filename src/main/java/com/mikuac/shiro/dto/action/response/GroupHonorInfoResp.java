package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * Created on 2021/7/8.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class GroupHonorInfoResp {

    @JSONField(name = "group_id")
    private Long groupId;

    @JSONField(name = "current_talkative")
    private CurrentTalkative currentTalkative;

    @JSONField(name = "talkative_list")
    private List<OtherHonor> talkativeList;

    @JSONField(name = "performer_list")
    private List<OtherHonor> performerList;

    @JSONField(name = "legend_list")
    private List<OtherHonor> legendList;

    @JSONField(name = "strong_newbie_list")
    private List<OtherHonor> strongNewbieList;

    @JSONField(name = "emotion_list")
    private List<OtherHonor> emotionList;

    /**
     * 忘了是啥
     */
    @Data
    public static class CurrentTalkative {

        @JSONField(name = "user_id")
        private Long userId;

        @JSONField(name = "nickname")
        private String nickname;

        @JSONField(name = "avatar")
        private String avatar;

        @JSONField(name = "day_count")
        private Integer dayCount;

    }

    /**
     * 其它荣耀
     */
    @Data
    public static class OtherHonor {

        @JSONField(name = "user_id")
        private Long userId;

        @JSONField(name = "nickname")
        private String nickname;

        @JSONField(name = "avatar")
        private String avatar;

        @JSONField(name = "description")
        private String description;

    }

}
