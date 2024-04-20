package com.mikuac.shiro.action;

import com.mikuac.shiro.common.utils.Keyboard;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.ActionList;
import com.mikuac.shiro.dto.action.common.MsgId;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.model.ArrayMsg;

import java.util.List;

public interface LagrangeExtend {

    /**
     * 获取收藏表情
     *
     * @return 表情的下载 URL
     */
    ActionList<String> fetchCustomFace();

}
