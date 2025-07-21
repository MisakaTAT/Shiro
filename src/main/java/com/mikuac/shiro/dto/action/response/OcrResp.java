package com.mikuac.shiro.dto.action.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("texts")
    private List<TextDetection> texts;

    @JsonProperty("language")
    private String language;

    @Data
    private static class TextDetection {

        /**
         * 文本
         */
        @JsonProperty("text")
        private String text;

        /**
         * 置信度
         */
        @JsonProperty("confidence")
        private Integer confidence;

        /**
         * 坐标
         */
        @JsonProperty("coordinates")
        private List<Coordinate> coordinates;

    }

    @Data
    private static class Coordinate {

        @JsonProperty("x")
        private Long x;

        @JsonProperty("y")
        private Long y;

    }

}
