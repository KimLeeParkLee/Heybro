package com.heybro.heybro.skin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "피부 검사 응답 DTO")
public class SkinAnalysisDataResponseDto {
    private String measurement_id;
    private String provider;
    private String model_version;
    private Quality quality;
    private Metrics metrics;
    private List<String> skin_type;
    private Object by_region;
    private boolean cached;
    private int final_score;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "퀄리티")
    public static class Quality {
        private int lighting_score;
        private boolean makeup_detected;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "측정 지표")
    public static class Metrics {
        private MetricDetail oiliness;
        private MetricDetail hydration;
        private MetricDetail redness;
        private MetricDetail poreVisibility;
        private MetricDetail acne;
        private MetricDetail blackhead;
        private MetricDetail wrinkle;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "상세 지표")
    public static class MetricDetail {
        private int score;
        private String level;
        private double confidence;
    }
}
