package com.heybro.heybro.skin.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.Map;

// Face++ API의 전체 응답을 담는 클래스
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Face++ 응답 DTO")
public class FaceppResponseDto {
    private List<Face> faces;
    @JsonProperty("request_id")
    private String requestId;

    @Data
    public static class Face {
        private Attributes attributes;
    }

    @Data
    public static class Attributes {
        private SkinStatus skinstatus;
        private Map<String, Double> beauty; // 메이크업 여부 등을 담고 있음
        private Map<String, Double> facequality; // 조명 값 등을 담고 있음
    }

    // 최종 피부 분석 결과인 'skinstatus' 객체를 담는 클래스
    // 필드 이름은 Face++ 문서의 JSON 키와 정확히 일치시켜야 합니다.
    @Data
    public static class SkinStatus {
        private double health;
        private double stain;
        private double acne;
        @JsonProperty("dark_circle") // JSON 키가 스네이크 케이스인 경우
        private double darkCircle;
        // 문서에 따라 oiliness, hydration, redness 등이 있을 수 있습니다.
        // 없다면 다른 값(health 등)을 기반으로 추정해야 합니다.
        // 여기서는 예시로 직접적인 값이 있다고 가정하겠습니다.
        private double oiliness;
        private double hydration;
        private double redness;
        private double pore;
        private double wrinkle;
    }
}