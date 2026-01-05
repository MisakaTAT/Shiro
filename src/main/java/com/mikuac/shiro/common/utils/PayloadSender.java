package com.mikuac.shiro.common.utils;

import com.mikuac.shiro.constant.Connection;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class PayloadSender {

    private static final ConcurrentHashMap<String, Lock> SESSION_LOCKS = new ConcurrentHashMap<>();

    private final WebSocketSession session;
    private final Lock sessionLock;
    private final int timeout;

    private final CompletableFuture<JsonObjectWrapper> responseFuture = new CompletableFuture<>();

    public PayloadSender(WebSocketSession session, int timeout) {
        this.session = session;
        this.timeout = timeout;
        this.sessionLock = SESSION_LOCKS.computeIfAbsent(session.getId(), k -> new ReentrantLock());
    }

    private static JsonObjectWrapper createErrorResp(String message) {
        JsonObjectWrapper resp = new JsonObjectWrapper();
        resp.put(Connection.RESULT_STATUS_KEY, Connection.FAILED_STATUS);
        resp.put(Connection.RESULT_CODE, -1);
        resp.put(Connection.RESULT_WORDING, message);
        return resp;
    }

    public static void cleanupSessionLock(String sessionId) {
        SESSION_LOCKS.remove(sessionId);
    }

    public JsonObjectWrapper send(@NonNull JsonObjectWrapper payload) {
        if (!sendMessage(payload)) {
            return createErrorResp("Failed to send message");
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
            responseFuture.complete(createErrorResp("Failed to send message: " + e.getMessage()));
            return false;
        }
    }

    private JsonObjectWrapper waitForResponse() {
        try {
            JsonObjectWrapper result = responseFuture.get(timeout, TimeUnit.SECONDS);
            return result != null ? result : createErrorResp("Response is null");
        } catch (TimeoutException e) {
            log.warn("Timeout waiting for response");
            return createErrorResp("Timeout waiting for response");
        } catch (Exception e) {
            log.error("Error waiting for response: {}", e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return createErrorResp("Error waiting for response: " + e.getMessage());
        }
    }

    public void onCallback(JsonObjectWrapper resp) {
        responseFuture.complete(resp);
    }
}
