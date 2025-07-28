package com.mikuac.shiro.dto.event.notice;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("group_id")
    private Long groupId;

    @JsonProperty("file")
    private File file;

    /**
     * 文件实体
     */
    @Data
    public static class File {

        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("size")
        private Long size;

        @JsonProperty("busid")
        private Long busid;

        @JsonProperty("url")
        private String url;

    }

}
