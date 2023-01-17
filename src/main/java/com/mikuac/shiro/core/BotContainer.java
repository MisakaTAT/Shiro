package com.mikuac.shiro.core;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2021/7/7.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Component
@SuppressWarnings("squid:S1104")
public class BotContainer {

    /**
     * Bot容器
     */
    public Map<Long, Bot> robots = new ConcurrentHashMap<>();

}
