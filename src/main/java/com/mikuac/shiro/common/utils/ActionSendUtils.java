package com.mikuac.shiro.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * Created on 2021/7/7.
 *
 * @author Zero
 */
public class ActionSendUtils extends Thread {

    private final WebSocketSession session;

    private final Long doRequestTimeout;

    private JSONObject respJson;

    /**
     * 构造函数
     *
     * @param session          websocket session
     * @param doRequestTimeout 请求超时时间
     */
    public ActionSendUtils(WebSocketSession session, Long doRequestTimeout) {
        this.session = session;
        this.doRequestTimeout = doRequestTimeout;
    }

    /**
     * 发起请求
     *
     * @param apiJson 请求的Json参数
     * @return respJson
     * @throws IOException          IO异常
     * @throws InterruptedException Interrupted Exception
     */
    public JSONObject doRequest(JSONObject apiJson) throws IOException, InterruptedException {
        synchronized (session) {
            session.sendMessage(new TextMessage(apiJson.toJSONString()));
        }
        synchronized (this) {
            this.wait(doRequestTimeout);
        }
        return respJson;
    }

    /**
     * 等待回调
     *
     * @param respJson 响应的Json数据
     */
    public void onResponse(JSONObject respJson) {
        this.respJson = respJson;
        synchronized (this) {
            this.notify();
        }
    }

}
