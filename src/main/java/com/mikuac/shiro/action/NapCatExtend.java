package com.mikuac.shiro.action;

import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.response.GetMsgListResp;

public interface NapCatExtend {

    /**
     * 获取群聊历史消息记录
     *
     * @param groupId      指定的群聊
     * @param messageSeq   指定的消息id
     * @param count        获取的消息条数
     * @param reverseOrder 是否反转获取到的消息,false：返回的消息为正序；true:返回的消息为倒序
     * @return 返回的消息列表
     */
    ActionData<GetMsgListResp> getGroupMsgHistory(long groupId, Long messageSeq, int count, boolean reverseOrder);

    /**
     * 获取好友历史消息记录
     *
     * @param userId       指定的好友
     * @param messageSeq   指定的消息id
     * @param count        获取的消息条数
     * @param reverseOrder 是否反转获取到的消息,false：返回的消息为正序；true:返回的消息为倒序
     * @return 返回的消息列表
     */
    ActionData<GetMsgListResp> getFriendMsgHistory(long userId, Long messageSeq, int count, boolean reverseOrder);

}
