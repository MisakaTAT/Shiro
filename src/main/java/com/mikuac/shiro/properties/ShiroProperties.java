package com.mikuac.shiro.properties;

import com.mikuac.shiro.core.BotMessageEventInterceptor;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.core.DefaultBotMessageEventInterceptor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2021/8/12.
 *
 * @author Zero
 */
@Data
@Component
@ConfigurationProperties(prefix = "shiro")
public class ShiroProperties {

    /**
     * 插件列表
     */
    private List<Class<? extends BotPlugin>> pluginList = new ArrayList<>();

    /**
     * 拦截器
     */
    private Class<? extends BotMessageEventInterceptor> interceptor = DefaultBotMessageEventInterceptor.class;

    /**
     * 日志等级设置为 debug
     */
    private boolean debug = false;

}
