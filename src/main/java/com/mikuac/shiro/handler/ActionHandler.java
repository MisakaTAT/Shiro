package com.mikuac.shiro.handler;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.common.limit.RateLimiter;
import com.mikuac.shiro.common.utils.ActionSendUtils;
import com.mikuac.shiro.enums.ActionPath;
import com.mikuac.shiro.properties.RateLimiterProperties;
import com.mikuac.shiro.properties.WebSocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;
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
    private final Map<String, ActionSendUtils> apiCallbackMap = new HashMap<>();

    /**
     * WebSocket 配置
     */
    @Resource
    private WebSocketProperties webSocketProperties;

    /**
     * 限速器配置
     */
    @Resource
    private RateLimiterProperties rateLimiterProperties;

    /**
     * 限速器
     */
    @Resource
    private RateLimiter rateLimiter;

    /**
     * 用于标识请求，可以是任何类型的数据，OneBot 将会在调用结果中原样返回
     */
    private int echo = 0;

    /**
     * 处理响应结果
     *
     * @param respJson 回调结果
     */
    public void onReceiveActionResp(JSONObject respJson) {
        String echo = respJson.get("echo").toString();
        ActionSendUtils actionSendUtils = apiCallbackMap.get(echo);
        if (actionSendUtils != null) {
            // 唤醒挂起的线程
            actionSendUtils.onCallback(respJson);
            apiCallbackMap.remove(echo);
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
        if (rateLimiterProperties.isEnable()) {
            if (rateLimiterProperties.isAwaitTask()) {
                // 阻塞当前线程直到获取令牌成功
                if (!rateLimiter.acquire()) {
                    return null;
                }
            }
            if (!rateLimiterProperties.isAwaitTask() && !rateLimiter.tryAcquire()) {
                return null;
            }
        }
        if (!session.isOpen()) {
            return null;
        }
        JSONObject reqJson = generateReqJson(action, params);
        ActionSendUtils actionSendUtils = new ActionSendUtils(session, webSocketProperties.getRequestTimeout());
        apiCallbackMap.put(reqJson.getString("echo"), actionSendUtils);
        JSONObject result;
        try {
            result = actionSendUtils.send(reqJson);
        } catch (Exception e) {
            log.error("Request failed: {}", e.getMessage());
            result = new JSONObject();
            result.put("status", "failed");
            result.put("retcode", -1);
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
    private JSONObject generateReqJson(ActionPath action, Map<String, Object> params) {
        return new JSONObject() {{
            put("action", action.getPath());
            if (params != null && !params.isEmpty()) {
                put("params", params);
            }
            put("echo", echo++);
        }};
    }

}
