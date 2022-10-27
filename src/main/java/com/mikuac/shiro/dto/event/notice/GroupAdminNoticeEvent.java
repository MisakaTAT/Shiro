package com.mikuac.shiro.dto.event.notice;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Created on 2021/7/8.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class GroupAdminNoticeEvent extends NoticeEvent {

    /**
     * set、unset
     * 事件子类型, 分别表示设置和取消管理
     */
    @JSONField(name = "sub_type")
    private String subType;

    /**
     * 群号
     */
    @JSONField(name = "group_id")
    private long groupId;

    /**
     * 管理员 QQ 号
     */
    @JSONField(name = "user_id")
    private long userId;

}
