package com.mikuac.shiro.dto.event.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class GroupAddRequestEvent extends RequestEvent {

    @JsonProperty("sub_type")
    private String subType;

    @JsonProperty("group_id")
    private Long groupId;

    @JsonProperty("invitor_id")
    private Long invitorId;

}
