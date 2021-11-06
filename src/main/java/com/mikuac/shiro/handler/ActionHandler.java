package com.mikuac.shiro.handler;

import com.alibaba.fastjson.JSONObject;
import com.mikuac.shiro.common.limit.ActionRateLimiter;
import com.mikuac.shiro.common.utils.ActionSendUtils;
import com.mikuac.shiro.enums.ActionPath;
import com.mikuac.shiro.properties.ActionLimiterProperties;
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
    private ActionLimiterProperties actionLimiterProperties;

    /**
     * 限速器
     */
    @Resource
    private ActionRateLimiter actionRateLimiter;

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
     * @param session websocket session
     * @param action  请求url
     * @param params  请求参数
     * @return 结果
     */
    public JSONObject doActionRequest(WebSocketSession session, ActionPath action, JSONObject params) {
        if (actionLimiterProperties.isEnable() && !actionRateLimiter.tryAcquire()) {
            log.warn("Token get failed, Ignored this action.");
            return null;
        }
        if (!session.isOpen()) {
            return null;
        }
        JSONObject reqJson = generateReqJson(action, params);
        String echo = reqJson.getString("echo");
        ActionSendUtils actionSendUtils = new ActionSendUtils(session, webSocketProperties.getDoRequestTimeout());
        apiCallbackMap.put(echo, actionSendUtils);
        JSONObject result;
        try {
            result = actionSendUtils.doRequest(reqJson);
        } catch (Exception e) {
            log.error("Do action request failed: {}", e.getMessage());
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
    private JSONObject generateReqJson(ActionPath action, JSONObject params) {
        JSONObject reqJson = new JSONObject();
        reqJson.put("action", action.getPath());
        if (params != null) {
            reqJson.put("params", params);
        }
        reqJson.put("echo", echo++);
        return reqJson;
    }


}
