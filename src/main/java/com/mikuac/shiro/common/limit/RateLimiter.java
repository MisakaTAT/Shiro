package com.mikuac.shiro.common.limit;

import com.mikuac.shiro.properties.RateLimiterProperties;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 限速器（令牌桶）
 *
 * @author zero
 */
@Component
public class RateLimiter implements ApplicationRunner {

    private final RateLimiterProperties props;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);

                @Override
                public Thread newThread(@NonNull Runnable r) {
                    Thread thread = new Thread(r, "rate-limiter-token-generator-" + threadNumber.getAndIncrement());
                    thread.setDaemon(true);
                    return thread;
                }
            }
    );

    @Autowired
    public RateLimiter(RateLimiterProperties props) {
        if (props == null) {
            throw new IllegalArgumentException("RateLimiterProperties cannot be null");
        }
        if (props.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        if (props.getRate() <= 0) {
            throw new IllegalArgumentException("Rate must be positive");
        }
        if (props.getTimeout() < 0) {
            throw new IllegalArgumentException("Timeout cannot be negative");
        }
        this.props = props;
    }

    /**
     * 重入锁
     */
    private final Lock lock = new ReentrantLock();

    /**
     * 状态管理
     */
    private final Condition condition = lock.newCondition();

    /**
     * 桶内剩余令牌数量
     */
    private double currentTokenQuantities;

    /**
     * 令牌定时补充器
     */
    @Override
    public void run(ApplicationArguments args) {
        currentTokenQuantities = props.getCapacity();
        long intervalMs = 100; // 100毫秒补充一次
        double tokensPerInterval = props.getRate() / (1000.0 / intervalMs);
        scheduler.scheduleAtFixedRate(
                () -> supplementToken(tokensPerInterval),
                0,
                intervalMs,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * 获取令牌（阻塞式）
     *
     * @return 是否成功
     */
    public boolean acquire() {
        return acquire(1);
    }

    /**
     * 获取令牌（非阻塞）
     *
     * @return 是否成功
     */
    public boolean tryAcquire() {
        return tryAcquire(1);
    }

    /**
     * 获取令牌（非阻塞）
     *
     * @param permits 获取数量
     * @return 是否成功
     */
    public boolean tryAcquire(int permits) {
        lock.lock();
        try {
            return getToken(permits);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取令牌（阻塞式）
     *
     * @param permits 获取数量
     * @return 是否成功
     */
    public boolean acquire(int permits) {
        lock.lock();
        try {
            while (!getToken(permits)) {
                try {
                    boolean await = condition.await(props.getTimeout(), TimeUnit.SECONDS);
                    if (!await) {
                        return false;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取令牌
     *
     * @param permits 获取数量
     * @return 是否成功
     */
    private boolean getToken(int permits) {
        if (permits > currentTokenQuantities) {
            return false;
        }
        this.currentTokenQuantities -= permits;
        return true;
    }

    /**
     * 向桶内补充指定数量的令牌
     *
     * @param tokenAmount 要补充的令牌数量
     */
    private void supplementToken(double tokenAmount) {
        lock.lock();
        try {
            currentTokenQuantities = Math.min(currentTokenQuantities + tokenAmount, props.getCapacity());
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 释放资源
     */
    @PreDestroy
    public void destroy() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
