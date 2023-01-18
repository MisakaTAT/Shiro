package com.mikuac.shiro.common.utils;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
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
public class ActionSendUtils {

    private final long timeout;

    private final WebSocketSession session;

    public ActionSendUtils(WebSocketSession session, Long timeout) {
        this.session = session;
        this.timeout = timeout;
    }

    private Map<String, Object> resp;

    private final Lock lock = new ReentrantLock();

    private final Condition condition = lock.newCondition();

    public Map<String, Object> send(Map<String, Object> payload) {
        lock.lock();
        try {
            String json = JSONObject.toJSONString(payload);
            session.sendMessage(new TextMessage(json));
            log.debug("[Action] {}", json);
            while (true) {
                boolean await = condition.await(timeout, TimeUnit.SECONDS);
                if (await) {
                    break;
                }
            }
        } catch (IOException e) {
            log.error("Action send exception: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
        return resp;
    }

    public void onCallback(Map<String, Object> resp) {
        lock.lock();
        try {
            this.resp = resp;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

}
