package com.mikuac.shiro.handler;

import com.mikuac.shiro.common.limit.RateLimiter;
import com.mikuac.shiro.common.utils.ConnectionUtils;
import com.mikuac.shiro.common.utils.JsonObjectWrapper;
import com.mikuac.shiro.common.utils.JsonUtils;
import com.mikuac.shiro.common.utils.PayloadSender;
import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.enums.ActionPath;
import com.mikuac.shiro.enums.AdapterEnum;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.enums.SessionStatusEnum;
import com.mikuac.shiro.exception.ShiroException;
import com.mikuac.shiro.model.ArrayMsg;
import com.mikuac.shiro.properties.RateLimiterProperties;
import com.mikuac.shiro.properties.WebSocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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
    private final Map<String, PayloadSender> callback = new ConcurrentHashMap<>();

    /**
     * WebSocket 配置
     */
    private final WebSocketProperties wsProp;

    /**
     * 限速器配置
     */
    private final RateLimiterProperties rateLimiterProps;

    /**
     * 限速器
     */
    private final RateLimiter rateLimiter;

    /**
     * 用于标识请求，可以是任何类型的数据，OneBot 将会在调用结果中原样返回
     */
    private final AtomicInteger echo = new AtomicInteger();

    @Autowired
    public ActionHandler(WebSocketProperties wsProp, RateLimiterProperties rateLimiterProps, RateLimiter rateLimiter) {
        this.wsProp = wsProp;
        this.rateLimiterProps = rateLimiterProps;
        this.rateLimiter = rateLimiter;
    }

    /**
     * 处理响应结果
     *
     * @param resp 回调结果
     */
    public void onReceiveActionResp(JsonObjectWrapper resp) {
        String e = resp.get("echo").toString();
        PayloadSender sender = callback.get(e);
        if (sender != null) {
            // 唤醒挂起的线程
            sender.onCallback(resp);
            callback.remove(e);
        }
    }

    /**
     * 处理带 keyboard 的请求
     *
     * @param session Session
     * @param action  请求路径
     * @param params  请求参数
     * @return 请求结果
     */
    public JsonObjectWrapper action(WebSocketSession session, ActionPath action, Map<String, Object> params) {
        return act(session, action, keyboardSerialize(params));
    }

    /**
     * 直接进行原始请求
     *
     * @param session Session
     * @param action  请求路径
     * @param params  请求参数
     * @return 请求结果
     */
    public JsonObjectWrapper rawAction(WebSocketSession session, ActionPath action, Map<String, Object> params) {
        return act(session, action, params);
    }

    private JsonObjectWrapper act(WebSocketSession session, ActionPath action, Map<String, Object> params) {
        JsonObjectWrapper result = new JsonObjectWrapper();
        if (Boolean.TRUE.equals(rateLimiterProps.getEnable())) {
            if (Boolean.TRUE.equals(rateLimiterProps.getAwaitTask()) && !rateLimiter.acquire()) {
                // 阻塞当前线程直到获取令牌成功
                return result;
            }
            if (Boolean.FALSE.equals(rateLimiterProps.getAwaitTask()) && !rateLimiter.tryAcquire()) {
                return result;
            }
        }

        if (ConnectionUtils.getAdapter(session) == AdapterEnum.SERVER) {
            SessionStatusEnum status = ConnectionUtils.getSessionStatus(session);
            if (!status.equals(SessionStatusEnum.ONLINE)) {
                if (status.equals(SessionStatusEnum.DIE)) {
                    throw new ShiroException.SessionCloseException();
                } else {
                    throw new ShiroException.SendMessageException();
                }
            }
        }

        JsonObjectWrapper payload = generatePayload(action, params);
        PayloadSender sender = new PayloadSender(session, wsProp.getTimeout());
        callback.put(payload.get("echo").toString(), sender);
        try {
            result = sender.send(payload);
        } catch (Exception e) {
            result.clear();
            result.put("status", "failed");
            result.put("retcode", -1);
            long xSelfId = ConnectionUtils.parseSelfId(session);
            log.error("API call failed [{}]: {}", xSelfId, result.get("wording"));
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
    private JsonObjectWrapper generatePayload(ActionPath action, Map<String, Object> params) {
        JsonObjectWrapper payload = new JsonObjectWrapper();
        payload.put("action", action.getPath());
        payload.put("echo", echo.getAndIncrement());
        if (params != null && !params.isEmpty()) {
            payload.put("params", params);
        }
        return payload;
    }

    private static Map<String, Object> keyboardSerialize(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return params;
        }

        Object msg = params.get(ActionParams.MESSAGE);
        if (!(msg instanceof List<?> original)) {
            return params;
        }

        List<Object> modified = original.stream().map(v -> {
            if (v instanceof ArrayMsg arrayMsg && arrayMsg.getType() == MsgTypeEnum.keyboard) {
                String data = arrayMsg.getStringData("keyboard");
                return JsonUtils.parseObject(data);
            }
            return v;
        }).toList();

        // 检查是否有过修改
        if (!modified.equals(original)) {
            Map<String, Object> copy = new HashMap<>(params);
            copy.put(ActionParams.MESSAGE, modified);
            return copy;
        }
        return params;
    }

}
