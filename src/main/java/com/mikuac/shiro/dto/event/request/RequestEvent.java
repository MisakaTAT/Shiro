package com.mikuac.shiro.dto.event.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("request_type")
    private String requestType;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("flag")
    private String flag;

}
