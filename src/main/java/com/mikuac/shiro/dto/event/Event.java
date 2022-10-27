package com.mikuac.shiro.dto.event;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 事件上报
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class Event {

    @JSONField(name = "post_type")
    private String postType;

    @JSONField(name = "time")
    private long time;

    @JSONField(name = "self_id")
    private long selfId;

}
