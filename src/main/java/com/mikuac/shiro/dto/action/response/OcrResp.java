package com.mikuac.shiro.dto.action.response;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * <p>OcrResp class.</p>
 *
 * @author zero
 * @version $Id: $Id
 */
@Data
public class OcrResp {

    @JSONField(name = "texts")
    private List<TextDetection> texts;

    @JSONField(name = "language")
    private String language;

    @Data
    private static class TextDetection {

        /**
         * 文本
         */
        @JSONField(name = "text")
        private String text;

        /**
         * 置信度
         */
        @JSONField(name = "confidence")
        private int confidence;

        /**
         * 坐标
         */
        @JSONField(name = "coordinates")
        private int[][] coordinates;

    }

}
