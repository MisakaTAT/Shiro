package com.mikuac.shiro.handler.event;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.enums.NotifyEventEnum;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author zero
 */
@Component
@SuppressWarnings("unused")
public class MetaEvent {

    /**
     * 存储元事件处理器
     */
    public final Map<String, BiConsumer<Bot, Map<String, Object>>> handlers = new HashMap<>();

    /**
     * 元事件分发
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     */
    public void handler(Bot bot, Map<String, Object> resp) {
        // Ignored this handler
    }

    /**
     * 事件处理
     *
     * @param bot  {@link Bot}
     * @param resp {@link JSONObject}
     * @param type {@link NotifyEventEnum}
     */
    @SuppressWarnings("unsed")
    private void process(Bot bot, Map<String, Object> resp, NotifyEventEnum type) {
        // Ignored this process
    }

}
