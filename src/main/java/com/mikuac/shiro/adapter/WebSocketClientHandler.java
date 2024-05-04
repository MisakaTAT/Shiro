package com.mikuac.shiro.adapter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.common.utils.CommonUtils;
import com.mikuac.shiro.common.utils.ConnectionUtils;
import com.mikuac.shiro.constant.Connection;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.core.CoreEvent;
import com.mikuac.shiro.enums.AdapterEnum;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.handler.EventHandler;
import com.mikuac.shiro.task.ShiroAsyncTask;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;

/**
 * Created on 2021/7/16.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Slf4j
public class WebSocketClientHandler extends TextWebSocketHandler {

    private final EventHandler eventHandler;

    private final BotFactory botFactory;

    private final ActionHandler actionHandler;

    private final ShiroAsyncTask shiroAsyncTask;

    private final BotContainer botContainer;

    private CoreEvent coreEvent;

    public WebSocketClientHandler(EventHandler eventHandler, BotFactory botFactory, ActionHandler actionHandler, ShiroAsyncTask shiroAsyncTask, BotContainer botContainer) {
        this.eventHandler = eventHandler;
        this.botFactory = botFactory;
        this.actionHandler = actionHandler;
        this.shiroAsyncTask = shiroAsyncTask;
        this.botContainer = botContainer;
    }

    @Autowired
    public void setCoreEvent(CoreEvent coreEvent) {
        this.coreEvent = coreEvent;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        session.setTextMessageSizeLimit(1024 * 1024);
        session.setBinaryMessageSizeLimit(1024 * 1024);

        try {
            session.getAttributes().put(Connection.ADAPTER_KEY, AdapterEnum.CLIENT);
            long xSelfId = ConnectionUtils.parseSelfId(session);
            if (xSelfId == 0L) {
                return;
            }
            if (!coreEvent.session(session)) {
                session.close();
                return;
            }
            Bot bot = botFactory.createBot(xSelfId, session);
            botContainer.robots.put(xSelfId, bot);
            log.info("Account {} connected", xSelfId);
            coreEvent.online(bot);
        } catch (IOException e) {
            log.error("Failed close websocket session: {}", e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        Map.Entry<Long, Bot> bot = botContainer.robots.entrySet().stream().findFirst().orElse(null);
        if (bot != null) {
            log.warn("Account {} disconnected", bot.getKey());
            coreEvent.offline(bot.getKey());
            botContainer.robots.clear();
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        long xSelfId = ConnectionUtils.parseSelfId(session);
        if (xSelfId == 0L) {
            boolean valid = JSON.isValid(message.getPayload());
            if (valid) {
                String selfId = JSONObject.parseObject(message.getPayload()).getOrDefault("self_id", "").toString();
                session.getAttributes().put("x-self-id", selfId);
            }
            if (!botContainer.robots.containsKey(xSelfId)) {
                afterConnectionEstablished(session);
            }
        }
        JSONObject result = JSON.parseObject(message.getPayload());
        log.debug("[Event] {}", CommonUtils.debugMsgDeleteBase64Content(result.toJSONString()));
        // if resp contains echo field, this resp is action resp, else event resp.
        if (result.containsKey(Connection.API_RESULT_KEY)) {
            if (Connection.FAILED_STATUS.equals(result.get(Connection.RESULT_STATUS_KEY))) {
                log.error("Action failed: {}", result.get("wording"));
            }
            actionHandler.onReceiveActionResp(result);
        } else {
            shiroAsyncTask.execHandlerMsg(eventHandler, xSelfId, result);
        }
    }

}
