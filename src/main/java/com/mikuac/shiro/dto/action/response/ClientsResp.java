package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>ClientsResp class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
public class ClientsResp {

    @JsonProperty("clients")
    private List<Clients> clients;

    @Data
    private static class Clients {

        @JsonProperty("app_id")
        private Long appId;

        @JsonProperty("device_name")
        private String deviceName;

        @JsonProperty("device_kind")
        private String deviceKind;

    }

}
