package com.mikuac.shiro.common.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class PayloadSender {

    private static final ConcurrentHashMap<String, Lock> SESSION_LOCKS = new ConcurrentHashMap<>();

    private final WebSocketSession session;

    private final Lock sessionLock;

    private final int timeout;

    private final Lock lock = new ReentrantLock();

    private final Condition condition = lock.newCondition();

    private JsonObjectWrapper resp;

    public PayloadSender(WebSocketSession session, int timeout) {
        this.session = session;
        this.timeout = timeout;
        this.sessionLock = SESSION_LOCKS.computeIfAbsent(session.getId(), k -> new ReentrantLock());
    }

    public JsonObjectWrapper send(@NonNull JsonObjectWrapper payload) {
        if (!sendMessage(payload)) {
            return resp;
        }
        return waitForResponse();
    }

    private boolean sendMessage(@NonNull JsonObjectWrapper payload) {
        try {
            String json = payload.toJSONString();
            sessionLock.lock();
            try {
                session.sendMessage(new TextMessage(json));
            } finally {
                sessionLock.unlock();
            }
            log.debug("[Action] {}", CommonUtils.debugMsgDeleteBase64Content(json));
            return true;
        } catch (IOException e) {
            log.error("Failed to send message: {}", e.getMessage(), e);
            return false;
        }
    }

    private JsonObjectWrapper waitForResponse() {
        lock.lock();
        try {
            long startTime = System.currentTimeMillis();
            long remainingTime = timeout * 1000L;

            while (remainingTime > 0) {
                boolean signalReceived = condition.await(remainingTime, TimeUnit.MILLISECONDS);
                if (signalReceived) {
                    return resp;
                }
                remainingTime = (timeout * 1000L) - (System.currentTimeMillis() - startTime);
            }

            log.warn("Timeout waiting for response");
            return resp;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while waiting for response: {}", e.getMessage(), e);
            return resp;
        } finally {
            lock.unlock();
        }
    }

    public void onCallback(JsonObjectWrapper resp) {
        lock.lock();
        try {
            this.resp = resp;
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public static void cleanupSessionLock(String sessionId) {
        SESSION_LOCKS.remove(sessionId);
    }

}
