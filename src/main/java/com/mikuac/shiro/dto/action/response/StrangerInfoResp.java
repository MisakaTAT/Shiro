package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created on 2021/7/8.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class StrangerInfoResp {

    /**
     * QQ 号
     */
    @JsonProperty("user_id")
    private Long userId;

    /**
     * 昵称
     */
    @JsonProperty("nickname")
    private String nickname;

    /**
     * 性别 male 或 female 或 unknown
     */
    @JsonProperty("sex")
    private String sex;

    /**
     * 年龄
     */
    @JsonProperty("age")
    private Integer age;

    /**
     * qid id 身份卡
     */
    @JsonProperty("qid")
    private String qid;

    /**
     * 等级
     */
    @JsonProperty("level")
    private Integer level;

    /**
     * 在线天数？我猜的（
     */
    @JsonProperty("login_days")
    private Integer loginDays;

}
