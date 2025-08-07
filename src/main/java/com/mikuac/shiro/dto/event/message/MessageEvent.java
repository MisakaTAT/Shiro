package com.mikuac.shiro.dto.event.message;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.mikuac.shiro.common.utils.JsonUtils;
import com.mikuac.shiro.common.utils.MessageConverser;
import com.mikuac.shiro.dto.event.Event;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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

    @JsonIgnore
    private String message;

    @JsonProperty("raw_message")
    private String rawMessage;

    @JsonProperty("font")
    private Integer font;

    @JsonIgnore
    private List<ArrayMsg> arrayMsg;

    @JsonSetter("message")
    private void setMessageFromJson(JsonNode json) {
        if (json.isTextual()) {
            this.message = json.asText();
            this.arrayMsg = MessageConverser.stringToArray(message);
        } else if (json.isArray()) {
            this.arrayMsg = JsonUtils.parseArray(json, ArrayMsg.class);
            message = MessageConverser.arraysToString(this.arrayMsg);
        } else {
            throw new IllegalArgumentException("Invalid message format: " + json);
        }
    }

    @JsonIgnore
    public void setMessage(String message) {
        this.message = message;
        this.arrayMsg = MessageConverser.stringToArray(message);
    }

    @JsonGetter("message")
    public String getMessage() {
        if (!StringUtils.hasText(message) && !CollectionUtils.isEmpty(arrayMsg)) {
            message = MessageConverser.arraysToString(arrayMsg);
        }
        return message;
    }

}
