package com.mikuac.shiro.dto.event.meta;

import com.alibaba.fastjson2.annotation.JSONField;
import com.mikuac.shiro.dto.event.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class MetaEvent extends Event {

    @JSONField(name = "time")
    private Long time;

    @JSONField(name = "self_id")
    private Long selfId;

}
