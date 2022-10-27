package com.mikuac.shiro.dto.event.request;

import com.alibaba.fastjson2.annotation.JSONField;
import com.mikuac.shiro.dto.event.Event;
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
public class RequestEvent extends Event {

    @JSONField(name = "request_type")
    private String requestType;

    @JSONField(name = "user_id")
    private long userId;

    @JSONField(name = "comment")
    private String comment;

    @JSONField(name = "flag")
    private String flag;

}
