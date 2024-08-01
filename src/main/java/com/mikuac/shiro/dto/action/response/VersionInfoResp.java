package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class VersionInfoResp {

    @JSONField(name = "app_name")
    private String appName;

    @JSONField(name = "app_version")
    private String appVersion;

    @JSONField(name = "protocol_version")
    private String protocolVersion;

    @JSONField(name = "version")
    private String version;
}
