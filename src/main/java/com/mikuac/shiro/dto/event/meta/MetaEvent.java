package com.mikuac.shiro.dto.event.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("time")
    private Long time;

    @JsonProperty("self_id")
    private Long selfId;

}
