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
import com.mikuac.shiro.enums.SessionStatusEnum;
import com.mikuac.shiro.handler.ActionHandler;
import com.mikuac.shiro.handler.EventHandler;
import com.mikuac.shiro.properties.WebSocketProperties;
import com.mikuac.shiro.task.ShiroAsyncTask;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2021/7/16.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Slf4j
public class WebSocketServer extends TextWebSocketHandler {

    @Setter
    private static int waitWebsocketConnect = 0;

    private final EventHandler eventHandler;

    private final BotFactory botFactory;

    private final ActionHandler actionHandler;

    private final ShiroAsyncTask shiroAsyncTask;

    private final BotContainer botContainer;

    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    public void setScheduledExecutorService(ThreadPoolTaskExecutor shiroTaskExecutor) {
        var executor = new ScheduledThreadPoolExecutor(shiroTaskExecutor.getCorePoolSize(),
                shiroTaskExecutor.getThreadPoolExecutor().getThreadFactory());
        executor.setRemoveOnCancelPolicy(true);
        scheduledExecutorService = executor;
    }

    private CoreEvent coreEvent;

    @Autowired
    public void setCoreEvent(CoreEvent coreEvent) {
        this.coreEvent = coreEvent;
    }

    private WebSocketProperties wsProp;

    @Autowired
    public void setWebSocketProperties(WebSocketProperties wsProp) {
        this.wsProp = wsProp;
    }

    public WebSocketServer(EventHandler eventHandler, BotFactory botFactory, ActionHandler actionHandler, ShiroAsyncTask shiroAsyncTask, BotContainer botContainer) {
        this.eventHandler = eventHandler;
        this.botFactory = botFactory;
        this.actionHandler = actionHandler;
        this.shiroAsyncTask = shiroAsyncTask;
        this.botContainer = botContainer;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        try {
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

            if (waitWebsocketConnect <= 0) {
                if (botContainer.robots.containsKey(xSelfId)) {
                    log.info("Bot {} already connected with another instance", xSelfId);
                    sessionContext.clear();
                    session.close();
                } else {
                    Bot bot = ConnectionUtils.handleFirstConnect(xSelfId, session, botFactory, coreEvent);
                    botContainer.robots.put(xSelfId, bot);
                }
                return;
            }
            botContainer.robots.compute(xSelfId, (id, bot) -> {
                if (Objects.isNull(bot)) {
                    bot = ConnectionUtils.handleFirstConnect(xSelfId, session, botFactory, coreEvent);
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

        if (waitWebsocketConnect <= 0) {
            sessionContext.clear();
            botContainer.robots.remove(xSelfId);
            log.warn("Account {} disconnected", xSelfId);
            coreEvent.offline(xSelfId);
            return;
        }
        // after the session is disconnected, postpone deletion instead of immediate removal
        // if not reconnected within a certain timeframe, execute the deletion scheduled task
        ScheduledFuture<?> removeSelfFuture = scheduledExecutorService.schedule(() -> {
            if (botContainer.robots.containsKey(xSelfId)) {
                botContainer.robots.remove(xSelfId);
                log.warn("Account {} disconnected", xSelfId);
                coreEvent.offline(xSelfId);
            }
        }, waitWebsocketConnect, TimeUnit.SECONDS);
        sessionContext.put(Connection.SESSION_STATUS_KEY, SessionStatusEnum.OFFLINE);
        sessionContext.put(Connection.FUTURE_KEY, removeSelfFuture);

    }

    @Override
    @SuppressWarnings("Duplicates")
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        long xSelfId = ConnectionUtils.parseSelfId(session);
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
