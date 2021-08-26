package com.mikuac.shiro.core;

import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.properties.PluginProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;

/**
 * Created on 2021/7/7.
 *
 * @author Zero
 */
@Component
public class BotFactory {

    @Resource
    private ActionHandler actionHandler;

    @Resource
    private PluginProperties pluginProperties;

    /**
     * 创建Bot对象
     *
     * @param selfId  Bot QQ
     * @param session websocket session
     * @return Bot对象
     */
    public Bot createBot(long selfId, WebSocketSession session) {
        return new Bot(selfId, session, actionHandler, pluginProperties.getPluginList());
    }

}
