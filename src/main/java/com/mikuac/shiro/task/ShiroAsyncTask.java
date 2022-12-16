package com.mikuac.shiro.task;

import com.alibaba.fastjson2.JSONObject;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.handler.Handler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 异步任务
 *
 * @author zero
 * @version $Id: $Id
 */
@Component
public class ShiroAsyncTask {

    @Resource
    private BotContainer botContainer;

    /**
     * 事件上报处理函数
     *
     * @param handler {@link Handler}
     * @param xSelfId      机器人 QQ
     * @param result       上报的 Json 数据
     */
    @Async("shiroTaskExecutor")
    public void execHandlerMsg(Handler handler, long xSelfId, JSONObject result) {
        handler.handler(botContainer.robots.get(xSelfId), result);
    }

}
