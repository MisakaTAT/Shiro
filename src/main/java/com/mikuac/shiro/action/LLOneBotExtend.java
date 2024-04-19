package com.mikuac.shiro.action;

import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.response.GroupFilesResp;

public interface LLOneBotExtend {

    /**
     * 获取群文件资源链接
     *
     * @param groupId 群号
     * @param fileId  文件ID
     * @param busId   文件类型
     * @return result {@link ActionData} of {@link GroupFilesResp}
     */
    ActionData<GroupFilesResp> getFile(long groupId, String fileId, int busId);

}
