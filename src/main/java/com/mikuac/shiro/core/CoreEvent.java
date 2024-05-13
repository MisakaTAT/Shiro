package com.mikuac.shiro.core;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * <p>CoreEvent class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Component
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public class CoreEvent {

    /**
     * 客户端上线事件
     * 可以做一些机器人上线事件，例如上线后发送消息给指定的群或好友
     * 如需获取上线的机器人账号可以调用 bot.getSelfId()
     *
     * @param bot {@link Bot}
     */
    public void online(Bot bot) {
        // do something...
    }

    /**
     * 客户端离线事件
     *
     * @param account 离线QQ号
     */
    public void offline(long account) {
        // do something...
    }

    /**
     * 可以通过 session.getHandshakeHeaders().getFirst("x-self-id") 获取上线的机器人账号
     * 例如当服务端为开放服务时，并且只有白名单内的账号才允许连接，此时可以检查账号是否存在于白名内
     * 不存在的话返回 false 即可禁止连接
     *
     * @param session {@link WebSocketSession}
     * @return 返回值为 false 时会中断 ws 会话
     */
    public boolean session(WebSocketSession session) {
        // do something...
        return true;
    }

}
