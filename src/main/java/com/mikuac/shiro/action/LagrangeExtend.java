package com.mikuac.shiro.action;

import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.ActionList;

import java.util.List;
import java.util.Map;

public interface LagrangeExtend {

    /**
     * 获取收藏表情
     *
     * @return 表情的下载 URL
     */
    ActionList<String> fetchCustomFace();

    /**
     * 发送合并转发
     *
     * @param msg 自定义转发消息 (可使用 ShiroUtils.generateForwardMsg() 方法创建)
     * @return result {@link ActionData} of {@link String} 合并转发的 longmsg Id
     */
    ActionData<String> sendForwardMsg(List<Map<String, Object>> msg);

}
