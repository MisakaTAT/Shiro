package com.mikuac.shiro.common.limit;

import com.google.common.util.concurrent.RateLimiter;
import com.mikuac.shiro.properties.ActionLimiterProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created on 2021/8/25.
 *
 * @author Zero
 */
@Slf4j
@Component
public class ActionRateLimiter implements ApplicationRunner {

    public static final String ACQUIRE = "acquire";

    public static final String TRY_ACQUIRE = "tryAcquire";

    @Resource
    private ActionLimiterProperties limiterProp;

    @SuppressWarnings("UnstableApiUsage")
    private RateLimiter rateLimiter;

    /**
     * 初始化限速器
     *
     * @param args ApplicationArguments
     */
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void run(ApplicationArguments args) {
        if (limiterProp.isEnable()) {
            int permitsPerSecond = limiterProp.getPermitsPerSecond();
            rateLimiter = RateLimiter.create(permitsPerSecond);
            log.info("Enable global action rate limiter, Current mode is {}", limiterProp.getMode());
            log.info("Current permits per second [{}]", permitsPerSecond);
        }
    }

    /**
     * 尝试获取1个令牌，如果获取失败，会阻塞当前线程，直到获取成功返回。
     *
     * @return 返回值为阻塞的秒数
     */
    @SuppressWarnings("UnstableApiUsage")
    public double acquire() {
        return rateLimiter.acquire();
    }

    /**
     * 尝试获取1个令牌，不会阻塞当前线程。
     *
     * @return 获取成功返回true，反之为false
     */
    @SuppressWarnings("UnstableApiUsage")
    public boolean tryAcquire() {
        return rateLimiter.tryAcquire();
    }

}