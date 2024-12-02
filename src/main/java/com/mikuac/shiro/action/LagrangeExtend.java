package com.mikuac.shiro.action;

import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.ActionList;
import com.mikuac.shiro.dto.action.common.ActionRaw;

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

    /**
     * 设置群消息表情回应
     *
     * @param groupId 群号
     * @param msgId   消息 ID
     * @param code    表情 ID
     * @param isAdd   添加/取消 回应
     * @return result {@link ActionRaw}
     */
    ActionRaw setGroupReaction(long groupId, int msgId, String code, boolean isAdd);

}
