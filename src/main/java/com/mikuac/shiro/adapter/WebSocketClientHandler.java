package com.mikuac.shiro.adapter;

import com.mikuac.shiro.common.utils.CommonUtils;
import com.mikuac.shiro.common.utils.ConnectionUtils;
import com.mikuac.shiro.common.utils.JsonObjectWrapper;
import com.mikuac.shiro.common.utils.JsonUtils;
import com.mikuac.shiro.constant.Connection;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.core.CoreEvent;
import com.mikuac.shiro.enums.AdapterEnum;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.handler.EventHandler;
import com.mikuac.shiro.properties.WebSocketProperties;
import com.mikuac.shiro.task.ShiroAsyncTask;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created on 2021/7/16.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Slf4j
public class WebSocketClientHandler extends TextWebSocketHandler {

    private final CoreEvent coreEvent;
    private final EventHandler eventHandler;
    private final BotFactory botFactory;
    private final ActionHandler actionHandler;
    private final ShiroAsyncTask shiroAsyncTask;
    private final BotContainer botContainer;
    private final WebSocketProperties wsProp;
    private final ThreadPoolTaskExecutor shiroTaskExecutor;

    @SuppressWarnings("squid:S107")
    public WebSocketClientHandler(
            EventHandler eventHandler, BotFactory botFactory, ActionHandler actionHandler,
            ShiroAsyncTask shiroAsyncTask, BotContainer botContainer, CoreEvent coreEvent,
            WebSocketProperties wsProp, ThreadPoolTaskExecutor shiroTaskExecutor
    ) {
        this.eventHandler = eventHandler;
        this.botFactory = botFactory;
        this.actionHandler = actionHandler;
        this.shiroAsyncTask = shiroAsyncTask;
        this.botContainer = botContainer;
        this.coreEvent = coreEvent;
        this.wsProp = wsProp;
        this.shiroTaskExecutor = shiroTaskExecutor;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        try {
            session.setTextMessageSizeLimit(wsProp.getMaxTextMessageBufferSize());
            session.setBinaryMessageSizeLimit(wsProp.getMaxBinaryMessageBufferSize());
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
            CompletableFuture.runAsync(() -> coreEvent.online(bot), shiroTaskExecutor);
        } catch (IOException e) {
            log.error("Failed close websocket session: {}", e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        Map.Entry<Long, Bot> bot = botContainer.robots.entrySet().stream().findFirst().orElse(null);
        if (bot != null) {
            log.warn("Account {} disconnected", bot.getKey());
            CompletableFuture.runAsync(() -> coreEvent.offline(bot.getKey()), shiroTaskExecutor);
            botContainer.robots.clear();
        }
    }

    @Override
    @SuppressWarnings("Duplicates")
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        long xSelfId = ConnectionUtils.parseSelfId(session);
        if (xSelfId == 0L) {
            boolean valid = JsonUtils.isValid(message.getPayload());
            if (valid) {
                String selfId = JsonObjectWrapper.parseObject(message.getPayload()).getOrDefault(Connection.SELF_ID, "").toString();
                session.getAttributes().put(Connection.X_SELF_ID, selfId);
                xSelfId = ConnectionUtils.parseSelfId(session);
            }
            if (!botContainer.robots.containsKey(xSelfId)) {
                afterConnectionEstablished(session);
            }
        }
        JsonObjectWrapper result = JsonObjectWrapper.parseObject(message.getPayload());
        log.debug("[Event] {}", CommonUtils.debugMsgDeleteBase64Content(result.toJSONString()));
        // if resp contains echo field, this resp is action resp, else event resp.
        if (result.containsKey(Connection.API_RESULT_KEY)) {
            if (Connection.FAILED_STATUS.equals(result.get(Connection.RESULT_STATUS_KEY))) {
                log.error("API call failed [{}]: {}", xSelfId, result.get("wording"));
            }
            actionHandler.onReceiveActionResp(result);
        } else {
            shiroAsyncTask.execHandlerMsg(eventHandler, xSelfId, result);
        }
    }

}
