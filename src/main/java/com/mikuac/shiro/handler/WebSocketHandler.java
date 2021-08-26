package com.mikuac.shiro.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.core.BotFactory;
import com.mikuac.shiro.task.ShiroAsyncTask;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Objects;

/**
 * Created on 2021/7/16.
 *
 * @author Zero
 */
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final static String API_RESULT_KEY = "echo";

    private final EventHandler eventHandler;

    private final BotFactory botFactory;

    private final ActionHandler actionHandler;

    private final ShiroAsyncTask shiroAsyncTask;

    private final BotContainer botContainer;

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

    private long parseSelfId(WebSocketSession session) {
        long xSelfId;
        try {
            xSelfId = Long.parseLong(Objects.requireNonNull(session.getHandshakeHeaders().get("x-self-id")).get(0));
        } catch (Exception e) {
            xSelfId = 0L;
        }
        return xSelfId;
    }

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) {
        long xSelfId = parseSelfId(session);
        if (xSelfId == 0L) {
            log.error("x-self-id get failed, close session");
            try {
                session.close();
            } catch (Exception e) {
                log.error("x-self-id is null, websocket session close exception");
            }
            return;
        }
        botContainer.robots.put(xSelfId, botFactory.createBot(xSelfId, session));
        log.info("account {} connected", xSelfId);
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        long xSelfId = parseSelfId(session);
        if (xSelfId == 0L) {
            return;
        }
        botContainer.robots.remove(xSelfId);
        log.info("account {} disconnected", xSelfId);
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
            actionHandler.onReceiveActionResp(result);
        } else {
            shiroAsyncTask.execHandlerMsg(eventHandler, xSelfId, result);
        }
    }

}
