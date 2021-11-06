package com.mikuac.shiro.common.limit;

import com.google.common.util.concurrent.RateLimiter;
import com.mikuac.shiro.properties.ActionLimiterProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Created on 2021/8/25.
 *
 * @author Zero
 */
@Slf4j
@Service
public class ActionRateLimiter {

    @Resource
    private ActionLimiterProperties actionLimiterProperties;

    @SuppressWarnings("UnstableApiUsage")
    private RateLimiter rateLimiter;

    /**
     * 创建限速器
     */
    @PostConstruct
    @DependsOn("actionLimiterProperties")
    @SuppressWarnings("UnstableApiUsage")
    private void createRateLimiter() {
        if (actionLimiterProperties.isEnable()) {
            int permitsPerSecond = actionLimiterProperties.getPermitsPerSecond();
            rateLimiter = RateLimiter.create(permitsPerSecond);
            log.info("Global rate limiter enable");
            log.info("The current permits per second [{}]", permitsPerSecond);
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
