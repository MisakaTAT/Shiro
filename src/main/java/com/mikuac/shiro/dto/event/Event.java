package com.mikuac.shiro.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("post_type")
    private String postType;

    @JsonProperty("time")
    private Long time;

    @JsonProperty("self_id")
    private Long selfId;

}
