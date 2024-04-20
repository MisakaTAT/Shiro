package com.mikuac.shiro.action;

import com.mikuac.shiro.dto.action.common.ActionList;

public interface LagrangeExtend {

    /**
     * 获取收藏表情
     *
     * @return 表情的下载 URL
     */
    ActionList<String> fetchCustomFace();

}
