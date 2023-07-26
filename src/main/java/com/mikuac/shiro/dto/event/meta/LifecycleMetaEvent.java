package com.mikuac.shiro.dto.event.meta;

import com.alibaba.fastjson2.annotation.JSONField;
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
public class LifecycleMetaEvent extends MetaEvent {

    /***
     * just is `enable`, `disable`, `connect`
     */
    @JSONField(name = "sub_type")
    private String subType;
}
