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
 * @version $Id: $Id
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class GroupUploadNoticeEvent extends NoticeEvent {

    @JSONField(name = "group_id")
    private Long groupId;

    @JSONField(name = "file")
    private File file;

    /**
     * 文件实体
     */
    @Data
    public static class File {

        @JSONField(name = "id")
        private String id;

        @JSONField(name = "name")
        private String name;

        @JSONField(name = "size")
        private Long size;

        @JSONField(name = "busid")
        private Long busid;

    }

}
