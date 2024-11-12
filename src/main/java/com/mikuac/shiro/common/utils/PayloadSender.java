package com.mikuac.shiro.common.utils;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
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
public class PayloadSender {

    private final int timeout;

    private final WebSocketSession session;

    public PayloadSender(WebSocketSession session, int timeout) {
        this.session = session;
        this.timeout = timeout;
    }

    private JSONObject resp;

    private final Lock lock = new ReentrantLock();

    private final Condition condition = lock.newCondition();

    public JSONObject send(@NonNull JSONObject payload) {
        lock.lock();
        try {
            String json = payload.toJSONString(JSONWriter.Feature.LargeObject);
            session.sendMessage(new TextMessage(json));
            log.debug("[Action] {}", CommonUtils.debugMsgDeleteBase64Content(json));
            long startTime = System.currentTimeMillis();
            long remainingTime = timeout * 1000L;
            while (remainingTime > 0) {
                boolean signalReceived = condition.await(remainingTime, TimeUnit.MILLISECONDS);
                if (signalReceived) {
                    // If we received the signal, return the response immediately
                    return resp;
                }
                // Recalculate remaining time
                remainingTime = (timeout * 1000L) - (System.currentTimeMillis() - startTime);
            }
            log.warn("Timeout waiting for response");
        } catch (IOException e) {
            log.error("Failed to send message: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while waiting for response: {}", e.getMessage(), e);
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
