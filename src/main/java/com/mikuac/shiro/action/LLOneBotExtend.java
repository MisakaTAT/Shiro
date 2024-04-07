package com.mikuac.shiro.action;

import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.response.GroupFilesResp;
import com.mikuac.shiro.dto.action.response.UrlResp;

public interface LLOneBotExtend {
    ActionData<GroupFilesResp> getFile(long groupId, String fileId, int busId);


}
