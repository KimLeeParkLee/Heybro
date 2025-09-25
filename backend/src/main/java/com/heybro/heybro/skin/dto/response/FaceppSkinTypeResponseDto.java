package com.heybro.heybro.skin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Face++ 피부 타입 응답 DTO")
public class FaceppSkinTypeResponseDto {
    @Schema(description = "결과")
    private ResultData result;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "결과 응답 DTO")
    public static class ResultData {
        @Schema(description = "피부 타입")
        private SkinTypeObject skinType;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "피부 타입 DTO")
    public static class SkinTypeObject {
        @Schema(description = "피부 타입 번호")
        private int skinType;

        @Schema(description = "피부 타입 정보 map")
        private Map<String, ConfidenceValueObject> details;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "신뢰도 DTO")
    public static class ConfidenceValueObject {
        @Schema(description = "신뢰도")
        private double confidence;

        @Schema(description = "선택", example = "1(선택)")
        private int value;
    }
}