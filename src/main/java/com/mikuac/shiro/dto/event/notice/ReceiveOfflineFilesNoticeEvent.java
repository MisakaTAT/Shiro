package com.mikuac.shiro.dto.event.notice;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Created on 2021/7/8.
 *
 * @author Zero
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class ReceiveOfflineFilesNoticeEvent extends NoticeEvent {

    @JSONField(name = "user_id")
    private long userId;

    @JSONField(name = "file")
    private File file;

    /**
     * 文件对象
     */
    @Data
    public static class File {

        @JSONField(name = "name")
        private String name;

        @JSONField(name = "size")
        private long size;

        @JSONField(name = "url")
        private String url;

    }

}
