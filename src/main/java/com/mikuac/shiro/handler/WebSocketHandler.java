package com.mikuac.shiro.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import java.util.List;

/**
 * Created on 2021/7/16.
 *
 * @author Zero
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
     * @param eventHandler   shiroEventHandler
     * @param botFactory     botFactory
     * @param actionHandler  shiroActionHandler
     * @param shiroAsyncTask asyncTask
     * @param botContainer   botContainer
     */
    public WebSocketHandler(EventHandler eventHandler, BotFactory botFactory, ActionHandler actionHandler,
                            ShiroAsyncTask shiroAsyncTask, BotContainer botContainer) {
        this.eventHandler = eventHandler;
        this.botFactory = botFactory;
        this.actionHandler = actionHandler;
        this.shiroAsyncTask = shiroAsyncTask;
        this.botContainer = botContainer;
    }

    /**
     * 获取连接的 QQ 号
     *
     * @param session WebSocketSession
     * @return QQ 号
     */
    private long parseSelfId(WebSocketSession session) {
        String key = "x-self-id";
        List<String> list = session.getHandshakeHeaders().get(key);
        if (list == null || list.size() <= 0) {
            return 0L;
        }
        return Long.parseLong(list.get(0));
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
        String key = "authorization";
        List<String> list = session.getHandshakeHeaders().get(key);
        if (list == null || list.size() <= 0) {
            return false;
        }
        return list.get(0).contains(token);
    }

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        long xSelfId = parseSelfId(session);
        if (xSelfId == 0L) {
            log.error("Close session, get x-self-id failed.");
            try {
                session.close();
            } catch (Exception e) {
                log.error("Websocket session close exception");
            }
            return;
        }
        if (!checkToken(session)) {
            log.error("Access token check failed");
            return;
        }
        botContainer.robots.put(xSelfId, botFactory.createBot(xSelfId, session));
        log.info("Account {} connected", xSelfId);
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        long xSelfId = parseSelfId(session);
        if (xSelfId == 0L) {
            return;
        }
        botContainer.robots.remove(xSelfId);
        log.warn("Account {} disconnected", xSelfId);
    }

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
        // 如果返回的Json中含有echo字段说明是api响应，否则当作事件上报处理
        if (result.containsKey(API_RESULT_KEY)) {
            if (FAILED_STATUS.equals(result.get(RESULT_STATUS_KEY))) {
                log.error("Message send failed: {}", result.get("wording"));
            }
            actionHandler.onReceiveActionResp(result);
        } else {
            shiroAsyncTask.execHandlerMsg(eventHandler, xSelfId, result);
        }
    }

}
