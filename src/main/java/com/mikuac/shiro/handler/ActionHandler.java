package com.mikuac.shiro.handler;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.common.limit.RateLimiter;
import com.mikuac.shiro.common.utils.ActionSendUtils;
import com.mikuac.shiro.enums.ActionPath;
import com.mikuac.shiro.properties.RateLimiterProperties;
import com.mikuac.shiro.properties.WebSocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private final Map<String, ActionSendUtils> apiCallbackMap = new HashMap<>();

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
        ActionSendUtils actionSendUtils = apiCallbackMap.get(e);
        if (actionSendUtils != null) {
            // 唤醒挂起的线程
            actionSendUtils.onCallback(resp);
            apiCallbackMap.remove(e);
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
    public Map<String, Object> action(WebSocketSession session, ActionPath action, Map<String, Object> params) {
        if (Boolean.TRUE.equals(rateLimiterProperties.getEnable())) {
            if (Boolean.TRUE.equals(rateLimiterProperties.getAwaitTask()) && !rateLimiter.acquire()) {
                // 阻塞当前线程直到获取令牌成功
                return Collections.emptyMap();
            }
            if (Boolean.TRUE.equals(!rateLimiterProperties.getAwaitTask()) && !rateLimiter.tryAcquire()) {
                return Collections.emptyMap();
            }
        }
        if (!session.isOpen()) {
            return Collections.emptyMap();
        }
        Map<String, Object> payload = generatePayload(action, params);
        ActionSendUtils actionSendUtils = new ActionSendUtils(session, webSocketProperties.getRequestTimeout());
        apiCallbackMap.put(payload.get("echo").toString(), actionSendUtils);
        Map<String, Object> result;
        try {
            result = actionSendUtils.send(payload);
        } catch (Exception e) {
            result = new LinkedHashMap<>();
            result.put("status", "failed");
            result.put("retcode", -1);
            Thread.currentThread().interrupt();
            log.error("Request failed: {}", e.getMessage());
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
    private Map<String, Object> generatePayload(ActionPath action, Map<String, Object> params) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("action", action.getPath());
        m.put("echo", echo++);
        if (params != null && !params.isEmpty()) {
            m.put("params", params);
        }
        return m;
    }

}
