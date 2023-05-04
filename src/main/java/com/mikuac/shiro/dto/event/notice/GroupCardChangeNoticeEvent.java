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
public class GroupCardChangeNoticeEvent extends NoticeEvent {

    @JSONField(name = "card_new")
    private String cardNew;

    @JSONField(name = "group_id")
    private Long groupId;

    @JSONField(name = "card_old")
    private String cardOld;

    @JSONField(name = "user_id")
    private Long userId;

}
