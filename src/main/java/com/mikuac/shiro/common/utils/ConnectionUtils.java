package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.constant.Connection;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.core.CoreEvent;
import com.mikuac.shiro.enums.SessionStatusEnum;
import com.mikuac.shiro.exception.ShiroException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.Optional;
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
        throw new ShiroException("session status type wrong");
    }

    @SneakyThrows
    public static void handleReConnect(Bot bot, long xSelfId, WebSocketSession session) {
        // this bot has connected before but was interrupted, updating its session
        // or handling simultaneous connections from different instances of the same account.
        log.info("Account {} reconnected", xSelfId);
        var oldSessionContext = bot.getSession().getAttributes();
        if (oldSessionContext.get(Connection.SESSION_STATUS_KEY) instanceof SessionStatusEnum status &&
                SessionStatusEnum.ONLINE.equals(status)) {
            // when multiple sessions with the same ID are connected
            session.close();
            return;
        }
        oldSessionContext.computeIfPresent(Connection.FUTURE_KEY, (e, obj) -> {
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

    @SneakyThrows
    public static Bot handleFirstConnect(long xSelfId, WebSocketSession session, BotFactory botFactory, CoreEvent coreEvent) {
        // if the session has never connected
        // or has been handled
        log.info("Account {} connected", xSelfId);
        var bot = botFactory.createBot(xSelfId, session);
        coreEvent.online(bot);
        return bot;
    }

    /**
     * 获取连接的 QQ 号
     *
     * @param session {@link WebSocketSession}
     * @return QQ 号
     */
    public static long parseSelfId(WebSocketSession session) {
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
    public static boolean checkToken(WebSocketSession session, String token) {
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

}
