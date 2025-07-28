package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("group_id")
    private Long groupId;

    @JsonProperty("current_talkative")
    private CurrentTalkative currentTalkative;

    @JsonProperty("talkative_list")
    private List<OtherHonor> talkativeList;

    @JsonProperty("performer_list")
    private List<OtherHonor> performerList;

    @JsonProperty("legend_list")
    private List<OtherHonor> legendList;

    @JsonProperty("strong_newbie_list")
    private List<OtherHonor> strongNewbieList;

    @JsonProperty("emotion_list")
    private List<OtherHonor> emotionList;

    /**
     * 忘了是啥
     */
    @Data
    public static class CurrentTalkative {

        @JsonProperty("user_id")
        private Long userId;

        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("avatar")
        private String avatar;

        @JsonProperty("day_count")
        private Integer dayCount;

    }

    /**
     * 其它荣耀
     */
    @Data
    public static class OtherHonor {

        @JsonProperty("user_id")
        private Long userId;

        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("avatar")
        private String avatar;

        @JsonProperty("description")
        private String description;

    }

}
