package com.mikuac.shiro.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.core.CoreEvent;
import com.mikuac.shiro.exception.ShiroException;
import com.mikuac.shiro.properties.WebSocketProperties;
import com.mikuac.shiro.task.ShiroAsyncTask;
import lombok.NonNull;
import lombok.SneakyThrows;
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
import java.util.Optional;
import java.util.concurrent.*;

/**
 * Created on 2021/7/16.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private static final String API_RESULT_KEY = "echo";

    private static final String FAILED_STATUS = "failed";

    private static final String RESULT_STATUS_KEY = "status";
    private static final String FUTURE_KEY        = "future";
    private static int WAIT_WEBSOCKET_CONNECT = 0;
    private static final String SESSION_STATUS_KEY        = "session_status";

    public enum SessionStatus {
        /**
         * 正常在线
         */
        Online,
        /**
         * 断开连接, 等待重连状态
         */
        Offline,
        /**
         * 断开连接, 不会恢复
         */
        Die
    }

    private final EventHandler eventHandler;

    private final BotFactory botFactory;

    private final ActionHandler actionHandler;

    private final ShiroAsyncTask shiroAsyncTask;

    private final BotContainer botContainer;

    private WebSocketProperties webSocketProperties;

    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    public void setScheduledExecutorService(ThreadPoolTaskExecutor shiroTaskExecutor) {
        var executor = new ScheduledThreadPoolExecutor(shiroTaskExecutor.getCorePoolSize(),
                shiroTaskExecutor.getThreadPoolExecutor().getThreadFactory());
        executor.setRemoveOnCancelPolicy(true);
        scheduledExecutorService = executor;
    }

    @Autowired
    public void setWebSocketProperties(WebSocketProperties webSocketProperties) {
        this.webSocketProperties = webSocketProperties;
    }

    private CoreEvent coreEvent;

    @Autowired
    public void setCoreEvent(CoreEvent coreEvent) {
        this.coreEvent = coreEvent;
    }

    /**
     * 构造函数
     *
     * @param eventHandler   {@link EventHandler}
     * @param botFactory     {@link BotFactory}
     * @param actionHandler  {@link ActionHandler}
     * @param shiroAsyncTask {@link ShiroAsyncTask}
     * @param botContainer   {@link BotContainer}
     */
    public WebSocketHandler(EventHandler eventHandler, BotFactory botFactory, ActionHandler actionHandler, ShiroAsyncTask shiroAsyncTask, BotContainer botContainer) {
        this.eventHandler = eventHandler;
        this.botFactory = botFactory;
        this.actionHandler = actionHandler;
        this.shiroAsyncTask = shiroAsyncTask;
        this.botContainer = botContainer;
    }

    /**
     * 获取连接的 QQ 号
     *
     * @param session {@link WebSocketSession}
     * @return QQ 号
     */
    private long parseSelfId(WebSocketSession session) {
        Optional<String> opt = Optional.ofNullable(session.getHandshakeHeaders().getFirst("x-self-id"));
        return opt.map(botId -> {
            try {
                return Long.parseLong(botId);
            } catch (NumberFormatException e) {
                return 0L;
            }
        }).orElse(0L);
    }

    /**
     * 访问密钥检查
     *
     * @param session WebSocketSession
     * @return 是否验证通过
     */
    private boolean checkToken(WebSocketSession session) {
        String token = webSocketProperties.getAccessToken();
        if (token.isEmpty()) {
            return true;
        }
        String clientToken = session.getHandshakeHeaders().getFirst("authorization");
        log.debug("Access Token: {}", clientToken);
        if (clientToken == null || clientToken.isEmpty()) {
            return false;
        }
        return token.equals(clientToken);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        try {
            long xSelfId = parseSelfId(session);
            if (xSelfId == 0L) {
                log.error("Account get failed for client");
                session.close();
                return;
            }
            if (! checkToken(session)) {
                log.error("Access token invalid");
                session.close();
                return;
            }
            if (! coreEvent.session(session)) {
                session.close();
                return;
            }
            var sessionContext = session.getAttributes();
            sessionContext.put(SESSION_STATUS_KEY, SessionStatus.Online);

            if (WAIT_WEBSOCKET_CONNECT <= 0) {
                if (botContainer.robots.containsKey(xSelfId)) {
                    log.info("Bot {} already connected with another instance", xSelfId);
                    sessionContext.clear();
                    session.close();
                } else {
                    this.handleFirstConnect(xSelfId, session);
                }
                return;
            }
            botContainer.robots.compute(xSelfId, (id, bot) -> {
                if (Objects.isNull(bot)) {
                    this.handleFirstConnect(xSelfId, session);
                } else {
                    this.handleReConnect(bot, xSelfId, session);
                }
                return bot;
            });
        } catch (IOException | ConcurrentModificationException e) {
            log.error("Websocket session close exception: {}", e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        long xSelfId = parseSelfId(session);
        if (xSelfId == 0L || !botContainer.robots.containsKey(xSelfId)) {
            return;
        }
        var sessionContext = session.getAttributes();

        if (WAIT_WEBSOCKET_CONNECT <= 0) {
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
        }, WAIT_WEBSOCKET_CONNECT, TimeUnit.SECONDS);
        sessionContext.put(SESSION_STATUS_KEY, SessionStatus.Offline);
        sessionContext.put(FUTURE_KEY, removeSelfFuture);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) {
        long xSelfId = parseSelfId(session);
        JSONObject result = JSON.parseObject(message.getPayload());
        log.debug("[Event] {}", result.toJSONString());
        // if resp contains echo field, this resp is action resp, else event resp.
        if (result.containsKey(API_RESULT_KEY)) {
            if (FAILED_STATUS.equals(result.get(RESULT_STATUS_KEY))) {
                log.error("Action failed: {}", result.get("wording"));
            }
            actionHandler.onReceiveActionResp(result);
        } else {
            shiroAsyncTask.execHandlerMsg(eventHandler, xSelfId, result);
        }
    }

    @SneakyThrows
    private void handleFirstConnect(long xSelfId, WebSocketSession session){
        // if the session has never connected
        // or has been handled
        log.info("Account {} connected", xSelfId);
        var bot = botFactory.createBot(xSelfId, session);
        coreEvent.online(bot);
        botContainer.robots.put(xSelfId, bot);
    }

    @SneakyThrows
    private void handleReConnect(Bot bot, long xSelfId, WebSocketSession session){
        // this bot has connected before but was interrupted, updating its session
        // or handling simultaneous connections from different instances of the same account.
        log.info("Account {} reconnected", xSelfId);
        var oldSessionContext = bot.getSession().getAttributes();
        if (oldSessionContext.get(SESSION_STATUS_KEY) instanceof SessionStatus status &&
                SessionStatus.Online.equals(status)) {
            // when multiple sessions with the same ID are connected
            session.close();
            return;
        }
        oldSessionContext.computeIfPresent(FUTURE_KEY, (e, obj) -> {
            // cancel the scheduled task of the bot
            if (obj instanceof ScheduledFuture<?> future && future.getDelay(TimeUnit.MILLISECONDS) > 0) {
                future.cancel(false);
            }
            return null;
        });
        // preventing memory leaks
        oldSessionContext.clear();
        bot.setSession(session);
    }

    public static SessionStatus getSessionStatus(WebSocketSession session) {
        var sessionContext = session.getAttributes();
        Object statusObj = sessionContext.getOrDefault(SESSION_STATUS_KEY, SessionStatus.Die);
        if (statusObj instanceof SessionStatus status) {
            return status;
        }
        // this type of exception will not occur unless there are external modifications to session.getAttributes()
        throw new ShiroException("session status type wrong");
    }

    public static void setWaitWebsocketConnect(int time) {
        WAIT_WEBSOCKET_CONNECT = time;
    }
}
