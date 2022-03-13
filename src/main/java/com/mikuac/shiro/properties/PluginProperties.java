package com.mikuac.shiro.properties;

import com.mikuac.shiro.core.BotMessageEventInterceptor;
import com.mikuac.shiro.core.BotPlugin;
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
public class PluginProperties {

    /**
     * 插件列表
     */
    List<Class<? extends BotPlugin>> pluginList = new ArrayList<>();

    Class<? extends BotMessageEventInterceptor> interceptor = BotMessageEventInterceptor.class;
}
