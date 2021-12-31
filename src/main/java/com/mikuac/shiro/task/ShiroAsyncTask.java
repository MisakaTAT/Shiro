package com.mikuac.shiro.task;

import com.alibaba.fastjson.JSONObject;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.handler.EventHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 异步任务
 *
 * @author zero
 */
@Component
public class ShiroAsyncTask {

    @Resource
    private BotContainer botContainer;

    /**
     * 事件上报处理函数
     *
     * @param eventHandler eventHandler
     * @param xSelfId      机器人QQ
     * @param result       上报的Json数据
     */
    @Async("shiroTaskExecutor")
    public void execHandlerMsg(EventHandler eventHandler, long xSelfId, JSONObject result) {
        try {
            eventHandler.handler(botContainer.robots.get(xSelfId), result);
        } catch (Exception ignored) {
        }
    }

}
