package com.mikuac.shiro.common.limit;

import com.mikuac.shiro.properties.RateLimiterProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    public RateLimiter(RateLimiterProperties props) {
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
    private long currentTokenQuantities;

    /**
     * 令牌定时补充器
     */
    @Override
    public void run(ApplicationArguments args) {
        currentTokenQuantities = props.getCapacity();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                supplementToken();
            }
        }, 0, 1000);
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
                boolean await = condition.await(props.getTimeout(), TimeUnit.SECONDS);
                if (await) {
                    return false;
                }
            }
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
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
     * 向桶内补充令牌
     */
    private void supplementToken() {
        lock.lock();
        try {
            currentTokenQuantities = Math.min(currentTokenQuantities + props.getRate(), props.getCapacity());
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

}
