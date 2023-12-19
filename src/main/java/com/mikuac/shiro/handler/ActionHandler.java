package com.mikuac.shiro.handler;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.common.limit.RateLimiter;
import com.mikuac.shiro.common.utils.SendUtils;
import com.mikuac.shiro.enums.ActionPath;
import com.mikuac.shiro.exception.ShiroException;
import com.mikuac.shiro.properties.RateLimiterProperties;
import com.mikuac.shiro.properties.WebSocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2021/7/7.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Slf4j
@Component
public class ActionHandler {

    /**
     * 请求回调数据
     */
    private final Map<String, SendUtils> callback = new HashMap<>();

    /**
     * WebSocket 配置
     */
    private WebSocketProperties webSocketProperties;

    @Autowired
    public void setWebSocketProperties(WebSocketProperties webSocketProperties) {
        this.webSocketProperties = webSocketProperties;
    }

    /**
     * 限速器配置
     */
    private RateLimiterProperties rateLimiterProperties;

    @Autowired
    public void setRateLimiterProperties(RateLimiterProperties rateLimiterProperties) {
        this.rateLimiterProperties = rateLimiterProperties;
    }

    /**
     * 限速器
     */
    private RateLimiter rateLimiter;

    @Autowired
    public void setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    /**
     * 用于标识请求，可以是任何类型的数据，OneBot 将会在调用结果中原样返回
     */
    private int echo = 0;

    /**
     * 处理响应结果
     *
     * @param resp 回调结果
     */
    public void onReceiveActionResp(JSONObject resp) {
        String e = resp.get("echo").toString();
        SendUtils sendUtils = callback.get(e);
        if (sendUtils != null) {
            // 唤醒挂起的线程
            sendUtils.onCallback(resp);
            callback.remove(e);
        }
    }

    /**
     * <p>action.</p>
     *
     * @param session Session
     * @param action  请求路径
     * @param params  请求参数
     * @return 请求结果
     */
    public JSONObject action(WebSocketSession session, ActionPath action, Map<String, Object> params) {
        JSONObject result = new JSONObject();
        if (Boolean.TRUE.equals(rateLimiterProperties.getEnable())) {
            if (Boolean.TRUE.equals(rateLimiterProperties.getAwaitTask()) && !rateLimiter.acquire()) {
                // 阻塞当前线程直到获取令牌成功
                return result;
            }
            if (Boolean.TRUE.equals(!rateLimiterProperties.getAwaitTask()) && !rateLimiter.tryAcquire()) {
                return result;
            }
        }
        WebSocketHandler.SessionStatus status;
        if (!(status = WebSocketHandler.getSessionStatus(session)).
                equals(WebSocketHandler.SessionStatus.Online)) {
            if (status.equals(WebSocketHandler.SessionStatus.Die)) {
                throw new ShiroException.SessionCloseException();
            } else {
                throw new ShiroException.SendMessageException();
            }

        }
        JSONObject payload = generatePayload(action, params);
        SendUtils sendUtils = new SendUtils(session, webSocketProperties.getTimeout());
        callback.put(payload.get("echo").toString(), sendUtils);
        try {
            result = sendUtils.send(payload);
        } catch (Exception e) {
            result.clear();
            result.put("status", "failed");
            result.put("retcode", -1);
            Thread.currentThread().interrupt();
            log.error("Action failed: {}", e.getMessage());
        }
        return result;
    }

    /**
     * 构建请求数据
     * {"action":"send_private_msg","params":{"user_id":10001000,"message":"你好"},"echo":"123"}
     *
     * @param action 请求路径
     * @param params 请求参数
     * @return 请求数据结构
     */
    private JSONObject generatePayload(ActionPath action, Map<String, Object> params) {
        JSONObject payload = new JSONObject();
        payload.put("action", action.getPath());
        payload.put("echo", echo++);
        if (params != null && !params.isEmpty()) {
            payload.put("params", params);
        }
        return payload;
    }

}
