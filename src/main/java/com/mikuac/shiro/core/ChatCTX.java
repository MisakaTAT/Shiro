package com.mikuac.shiro.core;

import com.mikuac.shiro.exception.ShiroException;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChatCTX {

    private static final String TIMEOUT_MESSAGE = "Chat context execute timeout";

    private int timeout;

    private AtomicBoolean hasTimedOut;

    private CompletableFuture<Void> lastTask;

    private ScheduledExecutorService scheduler;

    private ScheduledFuture<?> timeoutFuture;

    public ChatCTX create() {
        timeout = 0;
        lastTask = CompletableFuture.completedFuture(null);
        scheduler = Executors.newScheduledThreadPool(1);
        hasTimedOut = new AtomicBoolean(false);
        return this;
    }

    public ChatCTX next(Runnable block) {
        lastTask = lastTask.thenRunAsync(block);
        return this;
    }

    public ChatCTX timout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public void execute() {
        if (timeout > 0) {
            timeoutFuture = scheduler.schedule(
                    () -> lastTask.completeExceptionally(new ShiroException(TIMEOUT_MESSAGE)),
                    timeout,
                    TimeUnit.SECONDS
            );
        }

        try {
            lastTask.join();
        } catch (Exception e) {
            throw new ShiroException(TIMEOUT_MESSAGE);
        } finally {
            if (timeoutFuture != null) {
                timeoutFuture.cancel(false);
            }
            scheduler.shutdown();
        }
    }

    public void reset() {
        resetHandle(timeout);
    }

    public void reset(int timeout) {
        resetHandle(timeout);
    }

    private void resetHandle(int timeout) {
        if (timeoutFuture != null) {
            hasTimedOut.set(false);
            timeoutFuture.cancel(false);
            timeoutFuture = scheduler.schedule(() -> {
                if (!hasTimedOut.getAndSet(true)) {
                    lastTask.completeExceptionally(new ShiroException(TIMEOUT_MESSAGE));
                }
            }, timeout, TimeUnit.SECONDS);
        }
    }

}
