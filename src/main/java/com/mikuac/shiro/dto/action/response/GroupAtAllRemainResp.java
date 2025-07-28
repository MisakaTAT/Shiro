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
public class GroupAtAllRemainResp {

    /**
     * 是否可以 @全体成员
     */
    @JsonProperty("can_at_all")
    private Boolean canAtAll;

    /**
     * 群内所有管理当天剩余 @全体成员 次数
     */
    @JsonProperty("remain_at_all_count_for_group")
    private Integer remainAtAllCountForGroup;

    /**
     * Bot 当天剩余 @全体成员 次数
     */
    @JsonProperty("remain_at_all_count_for_uin")
    private Integer remainAtAllCountForUin;

}
