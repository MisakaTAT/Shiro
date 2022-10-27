package com.mikuac.shiro.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.properties.WebSocketProperties;
import com.mikuac.shiro.task.ShiroAsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Created on 2021/7/16.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final static String API_RESULT_KEY = "echo";

    private static final String FAILED_STATUS = "failed";

    private static final String RESULT_STATUS_KEY = "status";

    private final EventHandler eventHandler;

    private final BotFactory botFactory;

    private final ActionHandler actionHandler;

    private final ShiroAsyncTask shiroAsyncTask;

    private final BotContainer botContainer;

    @Resource
    private WebSocketProperties webSocketProperties;

    /**
     * 构造函数
     *
     * @param eventHandler   {@link com.mikuac.shiro.handler.EventHandler}
     * @param botFactory     {@link com.mikuac.shiro.core.BotFactory}
     * @param actionHandler  {@link com.mikuac.shiro.handler.ActionHandler}
     * @param shiroAsyncTask {@link com.mikuac.shiro.task.ShiroAsyncTask}
     * @param botContainer   {@link com.mikuac.shiro.core.BotContainer}
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
        String botId = session.getHandshakeHeaders().getFirst("x-self-id");
        if (botId == null || botId.isEmpty()) {
            return 0L;
        }
        try {
            return Long.parseLong(botId);
        } catch (Exception e) {
            return 0L;
        }
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
        if (clientToken == null || clientToken.isEmpty()) {
            return false;
        }
        return token.equals(clientToken);
    }

    /** {@inheritDoc} */
    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        try {
            long xSelfId = parseSelfId(session);
            if (xSelfId == 0L) {
                log.error("Get client self account failed");
                session.close();
                return;
            }
            if (!checkToken(session)) {
                log.error("Access token invalid");
                session.close();
                return;
            }
            botContainer.robots.put(xSelfId, botFactory.createBot(xSelfId, session));
            log.info("Account {} connected", xSelfId);
        } catch (IOException e) {
            log.error("Websocket session close exception");
            e.printStackTrace();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        long xSelfId = parseSelfId(session);
        if (xSelfId == 0L) {
            return;
        }
        botContainer.robots.remove(xSelfId);
        log.warn("Account {} disconnected", xSelfId);
    }

    /** {@inheritDoc} */
    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) {
        long xSelfId = parseSelfId(session);
        Bot bot = botContainer.robots.get(xSelfId);
        if (bot == null) {
            bot = botFactory.createBot(xSelfId, session);
            botContainer.robots.put(xSelfId, bot);
        }
        bot.setSession(session);
        JSONObject result = JSON.parseObject(message.getPayload());
        log.debug("[Event] {}", result.toJSONString());
        // if resp contains echo field, this resp is action resp, else event resp.
        if (result.containsKey(API_RESULT_KEY)) {
            if (FAILED_STATUS.equals(result.get(RESULT_STATUS_KEY))) {
                log.error("Request failed: {}", result.get("wording"));
            }
            actionHandler.onReceiveActionResp(result);
        } else {
            shiroAsyncTask.execHandlerMsg(eventHandler, xSelfId, result);
        }
    }

}
