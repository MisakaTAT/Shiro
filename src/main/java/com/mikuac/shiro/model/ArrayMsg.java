package com.mikuac.shiro.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mikuac.shiro.common.utils.JsonUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.enums.MsgTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author Zero
 * @version $Id: $Id
 */
@Data
@Accessors(chain = true)
public class ArrayMsg {

    @JsonIgnore
    private String type;

    @JsonProperty("data")
    private JsonNode data;

    @JsonIgnore
    public MsgTypeEnum getType() {
        return MsgTypeEnum.typeOf(type);
    }

    @JsonIgnore
    public ArrayMsg setType(MsgTypeEnum typeEnum) {
        if (typeEnum == null || !MsgTypeEnum.isValidMsgType(typeEnum)) {
            type = MsgTypeEnum.unknown.name();
        } else {
            type = typeEnum.name();
        }
        return this;
    }

    @JsonGetter("type")
    private String getTypeString() {
        return type;
    }

    @JsonSetter
    public void setTypeString(String type) {
        this.type = type;
    }

    public String getRawType() {
        return type;
    }

    public ArrayMsg setRawType(String type) {
        this.type = type;
        return this;
    }

    public ArrayMsg setData(Map<String, String> map) {
        if (data == null) {
            data = JsonUtils.getObjectMapper().createObjectNode();
        }
        map.forEach((key, value) -> {
            JsonNode valueNode;
            try {
                valueNode = JsonUtils.getObjectMapper().readTree(value);
            } catch (Exception e) {
                valueNode = JsonUtils.getObjectMapper().getNodeFactory().textNode(value);
            }
            ((ObjectNode) data).set(key, valueNode);
        });
        return this;
    }

    public String toCQCode() {
        if ("text".equalsIgnoreCase(type)) {
            return getStringData("text");
        }
        StringBuilder stringBuilder = new StringBuilder("[CQ:");
        stringBuilder.append(getRawType());
        data.properties().forEach((e) -> {

            stringBuilder.append(',');
            stringBuilder.append(e.getKey());
            stringBuilder.append('=');
            stringBuilder.append(ShiroUtils.escape(JsonUtils.nodeToString(e.getValue())));
        });
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    public long getLongData(String key) {
        var value = data.get(key);
        if (value == null || !value.isLong()) {
            return 0;
        }
        return value.asLong();
    }

    public String getStringData(String key) {
        var value = data.get(key);
        if (value == null) {
            return "";
        }
        return JsonUtils.nodeToString(value);
    }
}
