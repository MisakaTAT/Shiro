package com.mikuac.shiro.common.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

/**
 * Created on 2021/7/7.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Slf4j
@SuppressWarnings("all")
public class ActionSendUtils extends Thread {

    private final WebSocketSession session;

    private final long timeout;

    private JSONObject resp;

    /**
     * <p>Constructor for ActionSendUtils.</p>
     *
     * @param session {@link WebSocketSession}
     * @param timeout Request Timeout
     */
    public ActionSendUtils(WebSocketSession session, Long timeout) {
        this.session = session;
        this.timeout = timeout;
    }

    /**
     * <p>send.</p>
     *
     * @param payload json data
     * @return Response json data
     * @throws IOException          exception
     * @throws InterruptedException exception
     */
    public Map<String, Object> send(Map<String, Object> payload) throws IOException, InterruptedException {
        synchronized (session) {
            String json = JSONObject.toJSONString(payload);
            log.debug("[Action] {}", json);
            session.sendMessage(new TextMessage(json));
        }
        synchronized (this) {
            this.wait(timeout);
        }
        return resp;
    }

    /**
     * <p>onCallback.</p>
     *
     * @param resp Response json data
     */
    public void onCallback(JSONObject resp) {
        this.resp = resp;
        synchronized (this) {
            this.notify();
        }
    }

}
