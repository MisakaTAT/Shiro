package com.mikuac.shiro.properties;

import com.mikuac.shiro.core.BotMessageEventInterceptor;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.core.DefaultBotMessageEventInterceptor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created on 2021/8/12.
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
@Component
@ConfigurationProperties(prefix = "shiro")
public class ShiroProperties {

    /**
     * 插件列表
     */
    private List<Class<? extends BotPlugin>> pluginList = new CopyOnWriteArrayList<>();

    /**
     * 插件扫描路径（默认：项目根目录下的plugins目录）
     */
    private String pluginScanPath = "plugins";

    /**
     * 拦截器
     */
    private Class<? extends BotMessageEventInterceptor> interceptor = DefaultBotMessageEventInterceptor.class;

    /**
     * 如果多个 oneBot 实例同时连接,是否对相同的消息事件去重(先到优先),true 为仅触发第一个事件
     * 比如多个 bot 实例在同一个群中,同一个人发送的消息会在每个 bot 实例都触发一次事件,开启后将只收到第一个事件
     * 仅过滤群组消息,私聊消息不可能会重复
     */
    private Boolean groupEventFilter = false;

    /**
     * 在开启 groupEventFilter 的情况下,此选项控制是否过滤连接到框架的其他实例的qq号发出的消息
     */
    private Boolean groupSelfBotEventFilter = true;

    /**
     * 如果开启重复的群聊消息过滤,设定缓存的毫秒数时间
     * 也就是当多少毫秒之后如果受到重复的群聊消息 则剔除
     */
    private Integer groupEventFilterTime = 500;

    /**
     * 当发生掉线后, 等待重新连接的时间, 如果小于或等于0, 则不等待, 单位为秒数
     * 用于在处理 event 时, bot 因网络波动掉线, event 实例中的 bot 不可用(无法发送消息)
     * 开启后, 掉线后则等待重新上线, 若重新连接则可以继续发送消息
     * 注意, 如果掉线还未连接期间发送消息, 因为无法预见是否会重新连接, 所以发送仍然会失败, 仅重新连接成功后才正常发送消息
     * p.s. 一般用于bot连接不稳定, 且经常处理耗时任务, 防止断开后再也无法响应消息
     */
    private Integer waitBotConnect = 0;

    /**
     * 日志等级设置为 debug
     */
    private Boolean debug = false;
}
