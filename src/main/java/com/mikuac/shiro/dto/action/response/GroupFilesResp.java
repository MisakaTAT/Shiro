package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
     * 仅适用于LLOneBot
     */
    @JsonProperty("base64")
    private String base64;

    @JsonProperty("file")
    private String file;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("file_size")
    private Long fileSize;

    /**
     * 群文件
     */
    @Data
    public static class Files {

        @JsonProperty("file_id")
        private String fileId;

        @JsonProperty("file_name")
        private String fileName;

        @JsonProperty("busid")
        private Integer busId;

        @JsonProperty("file_size")
        private Long fileSize;

        @JsonProperty("upload_time")
        private Long uploadTime;

        @JsonProperty("dead_time")
        private Long deadTime;

        @JsonProperty("modify_time")
        private Long modifyTime;

        @JsonProperty("download_times")
        private Integer downloadTimes;

        @JsonProperty("uploader")
        private Long uploader;

        @JsonProperty("uploader_name")
        private String uploaderName;

    }

    /**
     * 群文件夹
     */
    @Data
    public static class Folders {

        @JsonProperty("folder_id")
        private String folderId;

        @JsonProperty("folder_name")
        private String folderName;

        @JsonProperty("create_time")
        private Long createTime;

        @JsonProperty("creator")
        private Long creator;

        @JsonProperty("creator_name")
        private String creatorName;

        @JsonProperty("total_file_count")
        private Integer totalFileCount;

    }

}
