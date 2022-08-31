package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * Created on 2021/7/8.
 *
 * @author Zero
 */
@Data
public class StrangerInfoResp {

    /**
     * QQ 号
     */
    @JSONField(name = "user_id")
    private long userId;

    /**
     * 昵称
     */
    @JSONField(name = "nickname")
    private String nickname;

    /**
     * 性别 male 或 female 或 unknown
     */
    @JSONField(name = "sex")
    private String sex;

    /**
     * 年龄
     */
    @JSONField(name = "age")
    private int age;

    /**
     * qid id 身份卡
     */
    @JSONField(name = "qid")
    private String qid;

    /**
     * 等级
     */
    @JSONField(name = "level")
    private int level;

    /**
     * 在线天数？我猜的（
     */
    @JSONField(name = "login_days")
    private int loginDays;

}
