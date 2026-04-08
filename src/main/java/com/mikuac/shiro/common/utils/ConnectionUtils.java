package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.constant.Connection;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.core.CoreEvent;
import com.mikuac.shiro.dto.event.meta.HeartbeatMetaEvent;
import com.mikuac.shiro.enums.AdapterEnum;
import com.mikuac.shiro.enums.SessionStatusEnum;
import com.mikuac.shiro.exception.ShiroException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConnectionUtils {

    private ConnectionUtils() {
    }

    public static SessionStatusEnum getSessionStatus(WebSocketSession session) {
        var sessionContext = session.getAttributes();
        Object statusObj = sessionContext.getOrDefault(Connection.SESSION_STATUS_KEY, SessionStatusEnum.DIE);
        if (statusObj instanceof SessionStatusEnum status) {
            return status;
        }
        // this type of exception will not occur unless there are external modifications to session.getAttributes()
        throw new ShiroException("Session status type wrong");
    }

    @SneakyThrows
    public static void handleReConnect(Bot bot, long xSelfId, WebSocketSession session, int heartbeatStaleMissCount) {
        log.info("Account {} reconnected", xSelfId);
        var oldSession = bot.getSession();
        var oldSessionContext = oldSession.getAttributes();
        if (oldSession.isOpen()
                && oldSessionContext.get(Connection.SESSION_STATUS_KEY) instanceof SessionStatusEnum status
                && SessionStatusEnum.ONLINE.equals(status)
                && isLiveDuplicateByHeartbeat(oldSession, heartbeatStaleMissCount)) {
            session.close();
            return;
        }
        cancelScheduledRemove(oldSessionContext);
        if (oldSession.isOpen() && !oldSession.getId().equals(session.getId())) {
            oldSession.close(CloseStatus.NORMAL);
            cancelScheduledRemove(oldSessionContext);
        }
        oldSessionContext.clear();
        bot.setSession(session);
    }

    public static void recordMetaHeartbeat(WebSocketSession session, HeartbeatMetaEvent event) {
        if (session == null || !session.isOpen()) {
            return;
        }
        var attrs = session.getAttributes();
        attrs.put(Connection.LAST_HEARTBEAT_AT_MS_KEY, System.currentTimeMillis());
        Long interval = event.getInterval();
        if (interval != null && interval > 0) {
            attrs.put(Connection.HEARTBEAT_INTERVAL_MS_KEY, interval);
        }
        if (event.getStatus() != null && event.getStatus().getOnline() != null) {
            attrs.put(Connection.LAST_HEARTBEAT_ONLINE_KEY, event.getStatus().getOnline());
        }
    }

    public static boolean isLiveDuplicateByHeartbeat(WebSocketSession session, int missCount) {
        var attrs = session.getAttributes();
        Object onlineFlag = attrs.get(Connection.LAST_HEARTBEAT_ONLINE_KEY);
        if (onlineFlag instanceof Boolean b && !b) {
            return false;
        }
        if (missCount <= 0) {
            return true;
        }
        Object last = attrs.get(Connection.LAST_HEARTBEAT_AT_MS_KEY);
        if (!(last instanceof Long lastAt)) {
            return true;
        }
        long interval = Connection.ONE_BOT_DEFAULT_HEARTBEAT_INTERVAL_MS;
        Object intervalObj = attrs.get(Connection.HEARTBEAT_INTERVAL_MS_KEY);
        if (intervalObj instanceof Long l && l > 0) {
            interval = l;
        }
        long threshold = interval * missCount + 2000L;
        return System.currentTimeMillis() - lastAt <= threshold;
    }

    private static void cancelScheduledRemove(Map<String, Object> sessionContext) {
        sessionContext.computeIfPresent(Connection.FUTURE_KEY, (e, obj) -> {
            if (obj instanceof ScheduledFuture<?> future && future.getDelay(TimeUnit.MILLISECONDS) > 0) {
                future.cancel(false);
            }
            return null;
        });
    }

    @SneakyThrows
    public static Bot handleFirstConnect(
            long xSelfId, WebSocketSession session,
            BotFactory botFactory, CoreEvent coreEvent,
            ThreadPoolTaskExecutor shiroTaskExecutor
    ) {
        // if the session has never connected
        // or has been handled
        log.info("Account {} connected", xSelfId);
        var bot = botFactory.createBot(xSelfId, session);
        CompletableFuture.runAsync(() -> coreEvent.online(bot), shiroTaskExecutor);
        return bot;
    }

    /**
     * 获取连接的 QQ 号
     *
     * @param session {@link WebSocketSession}
     * @return QQ 号
     */
    public static long parseSelfId(WebSocketSession session) {
        Object selfIdObj = session.getAttributes().get(Connection.X_SELF_ID);
        if (selfIdObj == null) {
            return 0L;
        }

        if (selfIdObj instanceof Long i) {
            return i;
        }

        try {
            return Long.parseLong(selfIdObj.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public static String getAuthorization(WebSocketSession session) {
        return session.getAttributes().getOrDefault("authorization", "").toString();
    }

    /**
     * 访问密钥检查
     *
     * @param session WebSocketSession
     * @return 是否验证通过
     */
    public static boolean checkToken(WebSocketSession session, String token) {
        if (token.isEmpty()) {
            return true;
        }
        String clientToken = getAuthorization(session);
        log.debug("Access Token: {}", clientToken);
        if (clientToken == null || clientToken.isEmpty()) {
            return false;
        }
        return token.equals(clientToken);
    }

    public static AdapterEnum getAdapter(WebSocketSession session) {
        var sessionContext = session.getAttributes();
        Object adapterObj = sessionContext.get(Connection.ADAPTER_KEY);
        if (adapterObj instanceof AdapterEnum adapter) {
            return adapter;
        }
        throw new ShiroException("Adapter type wrong");
    }

}
