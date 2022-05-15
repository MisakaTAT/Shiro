package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author Zero
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

        private int busid;

        @JSONField(name = "file_size")
        private long fileSize;

        @JSONField(name = "upload_time")
        private long uploadTime;

        @JSONField(name = "dead_time")
        private long deadTime;

        @JSONField(name = "modify_time")
        private long modifyTime;

        @JSONField(name = "download_times")
        private int downloadTimes;

        private long uploader;

        @JSONField(name = "uploader_name")
        private String uploaderName;

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
        private long createTime;

        private long creator;

        @JSONField(name = "creator_name")
        private String creatorName;

        @JSONField(name = "total_file_count")
        private int totalFileCount;

    }

}
