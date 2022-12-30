package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
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

    @JSONField(name = "clients")
    private List<Clients> clients;

    @Data
    private static class Clients {

        @JSONField(name = "app_id")
        private Long appId;

        @JSONField(name = "device_name")
        private String deviceName;

        @JSONField(name = "device_kind")
        private String deviceKind;

    }

}
