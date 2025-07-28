package com.mikuac.shiro.adapter;

import com.mikuac.shiro.common.utils.CommonUtils;
import com.mikuac.shiro.common.utils.ConnectionUtils;
import com.mikuac.shiro.common.utils.JsonObjectWrapper;
import com.mikuac.shiro.constant.Connection;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.core.CoreEvent;
import com.mikuac.shiro.enums.AdapterEnum;
import com.mikuac.shiro.enums.SessionStatusEnum;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.handler.EventHandler;
import com.mikuac.shiro.properties.ShiroProperties;
import com.mikuac.shiro.properties.WebSocketProperties;
import com.mikuac.shiro.task.ScheduledTask;
import com.mikuac.shiro.task.ShiroAsyncTask;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/7/16.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Slf4j
public class WebSocketServerHandler extends TextWebSocketHandler {

    private final EventHandler eventHandler;
    private final BotFactory botFactory;
    private final ActionHandler actionHandler;
    private final ShiroAsyncTask shiroAsyncTask;
    private final BotContainer botContainer;
    private final CoreEvent coreEvent;
    private final WebSocketProperties wsProp;
    private final ScheduledTask scheduledTask;
    private final ShiroProperties shiroProps;
    private final ThreadPoolTaskExecutor shiroTaskExecutor;

    @SuppressWarnings("java:S107")
    public WebSocketServerHandler(
            EventHandler eventHandler, BotFactory botFactory, ActionHandler actionHandler,
            ShiroAsyncTask shiroAsyncTask, BotContainer botContainer, CoreEvent coreEvent,
            WebSocketProperties wsProp, ScheduledTask scheduledTask, ShiroProperties shiroProps,
            ThreadPoolTaskExecutor shiroTaskExecutor
    ) {
        this.eventHandler = eventHandler;
        this.botFactory = botFactory;
        this.actionHandler = actionHandler;
        this.shiroAsyncTask = shiroAsyncTask;
        this.botContainer = botContainer;
        this.coreEvent = coreEvent;
        this.wsProp = wsProp;
        this.shiroProps = shiroProps;
        this.scheduledTask = scheduledTask;
        this.shiroTaskExecutor = shiroTaskExecutor;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        try {
            session.setTextMessageSizeLimit(wsProp.getMaxTextMessageBufferSize());
            session.setBinaryMessageSizeLimit(wsProp.getMaxBinaryMessageBufferSize());
            session.getAttributes().put(Connection.ADAPTER_KEY, AdapterEnum.SERVER);
            long xSelfId = ConnectionUtils.parseSelfId(session);
            if (xSelfId == 0L) {
                log.error("Failed parse x-self-id for websocket session");
                session.close();
                return;
            }
            if (!ConnectionUtils.checkToken(session, wsProp.getAccessToken())) {
                log.error("Invalid access token");
                session.close();
                return;
            }
            if (!coreEvent.session(session)) {
                session.close();
                return;
            }
            var sessionContext = session.getAttributes();
            sessionContext.put(Connection.SESSION_STATUS_KEY, SessionStatusEnum.ONLINE);
            if (shiroProps.getWaitBotConnect() <= 0) {
                if (botContainer.robots.containsKey(xSelfId)) {
                    log.info("Bot {} already connected with another instance", xSelfId);
                    sessionContext.clear();
                    session.close();
                } else {
                    Bot bot = ConnectionUtils.handleFirstConnect(xSelfId, session, botFactory, coreEvent, shiroTaskExecutor);
                    botContainer.robots.put(xSelfId, bot);
                }
                return;
            }
            // noinspection resource
            botContainer.robots.compute(xSelfId, (id, bot) -> {
                if (Objects.isNull(bot)) {
                    bot = ConnectionUtils.handleFirstConnect(xSelfId, session, botFactory, coreEvent, shiroTaskExecutor);
                } else {
                    ConnectionUtils.handleReConnect(bot, xSelfId, session);
                }
                return bot;
            });
        } catch (IOException | ConcurrentModificationException e) {
            log.error("Websocket session close exception: {}", e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        long xSelfId = ConnectionUtils.parseSelfId(session);
        if (xSelfId == 0L || !botContainer.robots.containsKey(xSelfId)) {
            return;
        }
        var sessionContext = session.getAttributes();

        if (shiroProps.getWaitBotConnect() <= 0) {
            sessionContext.clear();
            // noinspection resource
            botContainer.robots.remove(xSelfId);
            log.warn("Account {} disconnected", xSelfId);
            CompletableFuture.runAsync(() -> coreEvent.offline(xSelfId), shiroTaskExecutor);
            return;
        }
        // after the session is disconnected, postpone deletion instead of immediate removal
        // if not reconnected within a certain timeframe, execute the deletion scheduled task
        ScheduledFuture<?> removeSelfFuture = scheduledTask.executor().schedule(() -> {
            if (botContainer.robots.containsKey(xSelfId)) {
                // noinspection resource
                botContainer.robots.remove(xSelfId);
                log.warn("Account {} disconnected", xSelfId);
                CompletableFuture.runAsync(() -> coreEvent.offline(xSelfId), shiroTaskExecutor);
            }
        }, shiroProps.getWaitBotConnect(), TimeUnit.SECONDS);
        sessionContext.put(Connection.SESSION_STATUS_KEY, SessionStatusEnum.OFFLINE);
        sessionContext.put(Connection.FUTURE_KEY, removeSelfFuture);
    }

    @Override
    @SuppressWarnings("Duplicates")
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        long xSelfId = ConnectionUtils.parseSelfId(session);
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
