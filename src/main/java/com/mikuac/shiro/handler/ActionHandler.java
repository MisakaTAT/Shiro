package com.mikuac.shiro.handler;

import com.alibaba.fastjson.JSONObject;
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
 */
@Slf4j
@Component
public class ActionHandler {

    /**
     * api回调
     */
    private final Map<String, ActionSendUtils> apiCallbackMap = new HashMap<>();

    /**
     * websocket配置
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
     * 用于唯一标识一次请求，可以是任何类型的数据，OneBot 将会在调用结果中原样返回
     */
    private int echo = 0;

    /**
     * 处理action响应
     *
     * @param respJson action返回的json
     */
    public void onReceiveActionResp(JSONObject respJson) {
        String echo = respJson.get("echo").toString();
        ActionSendUtils actionSendUtils = apiCallbackMap.get(echo);
        if (actionSendUtils != null) {
            // 唤醒挂起的线程
            actionSendUtils.onResponse(respJson);
            apiCallbackMap.remove(echo);
        }
    }

    /**
     * 构建请求
     *
     * @param session {@link WebSocketSession}
     * @param action  {@link ActionPath}
     * @param params  请求参数
     * @return 结果
     */
    public JSONObject doActionRequest(WebSocketSession session, ActionPath action, Map<String, Object> params) {
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
        ActionSendUtils actionSendUtils = new ActionSendUtils(session, webSocketProperties.getDoRequestTimeout());
        apiCallbackMap.put(reqJson.getString("echo"), actionSendUtils);
        JSONObject result;
        try {
            result = actionSendUtils.doRequest(reqJson);
        } catch (Exception e) {
            log.error("Action request failed: {}", e.getMessage());
            result = new JSONObject();
            result.put("status", "failed");
            result.put("retcode", -1);
        }
        return result;
    }

    /**
     * 构造请求Json
     * {"action":"send_private_msg","params":{"user_id":10001000,"message":"你好"},"echo":"123"}
     *
     * @param action 请求路径
     * @param params 请求参数
     * @return json
     */
    private JSONObject generateReqJson(ActionPath action, Map<String, Object> params) {
        return new JSONObject() {{
            put("action", action.getPath());
            if (params != null) {
                put("params", params);
            }
            put("echo", echo++);
        }};
    }

}
