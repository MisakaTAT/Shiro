package com.mikuac.shiro.common.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created on 2021/7/7.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Slf4j
public class SendUtils {

    private final int timeout;

    private final WebSocketSession session;

    public SendUtils(WebSocketSession session, int timeout) {
        this.session = session;
        this.timeout = timeout;
    }

    private JSONObject resp;

    private final Lock lock = new ReentrantLock();

    private final Condition condition = lock.newCondition();

    @SuppressWarnings({"ResultOfMethodCallIgnored", "squid:S899", "squid:S2274"})
    public JSONObject send(@NonNull JSONObject payload) {
        lock.lock();
        try {
            String json = payload.toJSONString();
            session.sendMessage(new TextMessage(json));
            log.debug("[Action] {}", json);
            condition.await(payload.getIntValue("echo") == 0 ? 1 : timeout, TimeUnit.SECONDS);
        } catch (IOException e) {
            log.error("Send action payload exception: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        return resp;
    }

    public void onCallback(JSONObject resp) {
        lock.lock();
        try {
            this.resp = resp;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

}
