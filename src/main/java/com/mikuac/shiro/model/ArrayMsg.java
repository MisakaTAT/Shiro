package com.mikuac.shiro.model;

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

    private String type;

    private Map<String, String> data;

    public MsgTypeEnum getType() {
        return MsgTypeEnum.typeOf(type);
    }

    public ArrayMsg setType(MsgTypeEnum typeEnum) {
        if (typeEnum == null || !MsgTypeEnum.isValidMsgType(typeEnum)) {
            type = MsgTypeEnum.unknown.name();
        } else {
            type = typeEnum.name();
        }
        return this;
    }

    public String getRawType() {
        return type;
    }

    public ArrayMsg setRawType(String type) {
        this.type = type;
        return this;
    }

    public String toCQCode() {
        if ("text".equalsIgnoreCase(type)) {
            return data.getOrDefault("text", "");
        }
        StringBuilder stringBuilder = new StringBuilder("[CQ:");
        stringBuilder.append(getRawType());
        data.forEach((key, val) -> {
            stringBuilder.append(',');
            stringBuilder.append(key);
            stringBuilder.append('=');
            stringBuilder.append(val);
        });
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

}
