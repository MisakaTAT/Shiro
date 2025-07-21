package com.mikuac.shiro.dto.event.message;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.mikuac.shiro.common.utils.JsonUtils;
import com.mikuac.shiro.dto.event.Event;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * <p>MessageEvent class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class MessageEvent extends Event {

    @JsonProperty("message_type")
    private String messageType;

    @JsonProperty("user_id")
    private Long userId;

    private String message;

    @JsonProperty("raw_message")
    private String rawMessage;

    @JsonProperty("font")
    private Integer font;

    @JsonIgnore
    private List<ArrayMsg> arrayMsg;

    @JsonProperty("raw")
    private Raw raw;

    @JsonSetter("message")
    private void setMessageFromJson(JsonNode json) {
        if (json.isTextual()) {
            this.message = json.asText();
        } else if (json.isArray()) {
            this.arrayMsg = JsonUtils.parseArray(json, ArrayMsg.class);
            message = JsonUtils.toJSONString(json);
        } else {
            throw new IllegalArgumentException("Invalid message format: " + json);
        }
    }
    @JsonGetter("message")
    public String getMessage() {
        return message;
    }

    /**
     * Raw字段在napcat开启debug模式时会出现，其中有msgSeq字段。
     * 在单个群聊内，不同bot，收到同一条消息时，msgSeq是相同的，
     * 基于此可以实现群聊内多bot的均衡负载。
     * <p>
     * raw内还有更多数据...
     */
    @Data
    public static class Raw {
        private Long msgId;
        private Long msgRandom;
        private Integer msgSeq;
        private Integer chatType;
        private Integer msgType;
        private Integer subMsgType;
        private Integer sendType;
    }
}
