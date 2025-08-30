package com.heybro.heybro.skin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkinAnalysisDataResponseDto {
    private String measurement_id;
    private String provider;
    private String model_version;
    private String created_at;
    private Quality quality;
    private Metrics metrics;
    private List<String> skin_type;
    private Object by_region; // 구체적인 타입이 정해지면 변경
    private boolean cached;

    @Data
    public static class Quality {
        private int lighting_score;
        private boolean makeup_detected;
    }

    @Data
    public static class Metrics {
        private MetricDetail oiliness;
        private MetricDetail hydration;
        private MetricDetail redness;
        private MetricDetail pore_visibility;
        private MetricDetail acne;
        private MetricDetail wrinkle;
    }

    @Data
    public static class MetricDetail {
        private int score;
        private String level;
        private double confidence;
    }
}
