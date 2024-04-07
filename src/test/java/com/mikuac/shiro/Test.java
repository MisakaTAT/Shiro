package com.mikuac.shiro;

import com.mikuac.shiro.annotation.GroupUploadNoticeHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.notice.GroupUploadNoticeEvent;
import org.springframework.stereotype.Component;

import static com.mikuac.shiro.core.BotPlugin.MESSAGE_IGNORE;

@Shiro
@Component
public class Test {
    @GroupUploadNoticeHandler
    public int onGroupUploadNotice(Bot bot, GroupUploadNoticeEvent event) {
        System.out.println(bot.getSelfId());
        System.out.println(event.getFile().getId());
        return MESSAGE_IGNORE;
    }
}
