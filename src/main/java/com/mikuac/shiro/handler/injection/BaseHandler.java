package com.mikuac.shiro.handler.injection;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;

public interface BaseHandler {

     void invokeEvent(Bot bot, PrivateMessageEvent event) ;

}
