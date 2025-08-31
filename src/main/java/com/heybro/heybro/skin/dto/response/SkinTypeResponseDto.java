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
@Schema(description = "피부 타입 응답 DTO")
public class SkinTypeResponseDto {
    private String skinType;

    private Map<String, Detail> details;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "상세 정보")
    public static class Detail {
        private int value;
        private double confidence;
    }
}