package com.mikuac.shiro.dto.event.message;

import com.alibaba.fastjson2.annotation.JSONField;
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

    @JSONField(name = "message_type")
    private String messageType;

    @JSONField(name = "user_id")
    private Long userId;

    @JSONField(name = "message")
    private String message;

    @JSONField(name = "raw_message")
    private String rawMessage;

    @JSONField(name = "font")
    private Integer font;

    private List<ArrayMsg> arrayMsg;

    @JSONField(name = "raw")
    private Raw raw;


    /**
     * Raw字段在napcat开启debug模式时会出现，其中有msgSeq字段。
     * 在单个群聊内，不同bot，收到同一条消息时，msgSeq是相同的，
     * 基于此可以实现群聊内多bot的均衡负载。
     *
     * raw内还有更多数据...
     */
    @Data
    public static class Raw{
        private Long msgId;
        private Long msgRandom;
        private Integer msgSeq;
        private Integer chatType;
        private Integer msgType;
        private Integer subMsgType;
        private Integer sendType;
    }
}
