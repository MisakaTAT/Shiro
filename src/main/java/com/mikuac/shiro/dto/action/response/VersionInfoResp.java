package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VersionInfoResp {

    @JsonProperty("app_name")
    private String appName;

    @JsonProperty("app_version")
    private String appVersion;

    @JsonProperty("protocol_version")
    private String protocolVersion;

    @JsonProperty("version")
    private String version;
}
