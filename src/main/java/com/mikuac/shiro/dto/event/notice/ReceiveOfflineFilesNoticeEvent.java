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
public class ReceiveOfflineFilesNoticeEvent extends NoticeEvent {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("file")
    private File file;

    /**
     * 文件对象
     */
    @Data
    public static class File {

        @JsonProperty("name")
        private String name;

        @JsonProperty("size")
        private Long size;

        @JsonProperty("url")
        private String url;

    }

}
