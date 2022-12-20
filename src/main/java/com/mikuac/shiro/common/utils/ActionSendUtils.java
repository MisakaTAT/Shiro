package com.mikuac.shiro.common.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * Created on 2021/7/7.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Slf4j
public class ActionSendUtils extends Thread {

    private final WebSocketSession session;

    private final long requestTimeout;

    private JSONObject resp;

    /**
     * <p>Constructor for ActionSendUtils.</p>
     *
     * @param session        {@link WebSocketSession}
     * @param requestTimeout Request Timeout
     */
    public ActionSendUtils(WebSocketSession session, Long requestTimeout) {
        this.session = session;
        this.requestTimeout = requestTimeout;
    }

    /**
     * <p>send.</p>
     *
     * @param req Request json data
     * @return Response json data
     * @throws IOException          exception
     * @throws InterruptedException exception
     */
    public JSONObject send(JSONObject req) throws IOException, InterruptedException {
        synchronized (session) {
            log.debug("[Action] {}", req.toJSONString());
            session.sendMessage(new TextMessage(req.toJSONString()));
        }
        synchronized (this) {
            this.wait(requestTimeout);
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
