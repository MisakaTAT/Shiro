package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * <p>GroupFilesResp class.</p>
 *
 * @author Zero
 * @version $Id: $Id
 */
@Data
public class GroupFilesResp {

    private List<Files> files;

    private List<Folders> folders;

    /**
     * 群文件
     */
    @Data
    public static class Files {

        @JSONField(name = "file_id")
        private String fileId;

        @JSONField(name = "file_name")
        private String fileName;

        @JSONField(name = "busid")
        private Integer busId;

        @JSONField(name = "file_size")
        private Long fileSize;

        @JSONField(name = "upload_time")
        private Long uploadTime;

        @JSONField(name = "dead_time")
        private Long deadTime;

        @JSONField(name = "modify_time")
        private Long modifyTime;

        @JSONField(name = "download_times")
        private Integer downloadTimes;

        @JSONField(name = "uploader")
        private Long uploader;

        @JSONField(name = "uploader_name")
        private String uploaderName;

        @JSONField(name = "base64")
        private String base64;

        @JSONField(name = "file")
        private String file;

    }

    /**
     * 群文件夹
     */
    @Data
    public static class Folders {

        @JSONField(name = "folder_id")
        private String folderId;

        @JSONField(name = "folder_name")
        private String folderName;

        @JSONField(name = "create_time")
        private Long createTime;

        @JSONField(name = "creator")
        private Long creator;

        @JSONField(name = "creator_name")
        private String creatorName;

        @JSONField(name = "total_file_count")
        private Integer totalFileCount;

    }

}
