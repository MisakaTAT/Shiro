package com.mikuac.shiro.handler.event;

import com.mikuac.shiro.common.utils.JsonObjectWrapper;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.meta.HeartbeatMetaEvent;
import com.mikuac.shiro.dto.event.meta.LifecycleMetaEvent;
import com.mikuac.shiro.enums.MetaEventEnum;
import com.mikuac.shiro.enums.NotifyEventEnum;
import com.mikuac.shiro.handler.injection.InjectionHandler;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final InjectionHandler injection;

    @Autowired
    public MetaEvent(InjectionHandler injectionHandler) {
        this.injection = injectionHandler;
    }

    /**
     * 存储元事件处理器
     */
    public final Map<String, BiConsumer<Bot, JsonObjectWrapper>> handlers = new HashMap<>();

    /**
     * 元事件分发
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     */
    public void handler(Bot bot, JsonObjectWrapper resp) {
        String type = resp.getString("meta_event_type");
        handlers.getOrDefault(type, (b, e) -> {
        }).accept(bot, resp);
    }

    public void heartbeat(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, MetaEventEnum.HEARTBEAT);
    }

    public void lifecycle(Bot bot, JsonObjectWrapper resp) {
        process(bot, resp, MetaEventEnum.LIFECYCLE);
    }

    /**
     * 事件处理
     *
     * @param bot  {@link Bot}
     * @param resp {@link JsonObjectWrapper}
     * @param type {@link NotifyEventEnum}
     */
    @SuppressWarnings("unsed")
    private void process(Bot bot, JsonObjectWrapper resp, MetaEventEnum type) {
        switch (type) {
            case HEARTBEAT -> {
                HeartbeatMetaEvent event = resp.to(HeartbeatMetaEvent.class);
                injection.invokeHeartbeat(bot, event);
            }
            case LIFECYCLE -> {
                LifecycleMetaEvent event = resp.to(LifecycleMetaEvent.class);
                injection.invokeLifecycle(bot, event);
            }
            default -> {
                // Ignore
            }
        }
    }

}
