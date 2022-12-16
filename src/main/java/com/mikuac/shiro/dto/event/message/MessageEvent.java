package com.mikuac.shiro.dto.event.message;

import com.alibaba.fastjson2.annotation.JSONField;
import com.mikuac.shiro.bo.ArrayMsg;
import com.mikuac.shiro.dto.event.Event;
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

    @JSONField(name = "message_type")
    private String messageType;

    @JSONField(name = "user_id")
    private long userId;

    @JSONField(name = "message")
    private String message;

    @JSONField(name = "raw_message")
    private String rawMessage;

    @JSONField(name = "font")
    private int font;

    private List<ArrayMsg> arrayMsg;

}
