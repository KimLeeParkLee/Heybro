package com.heybro.heybro.skin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Face++ 응답 DTO")
public class FaceppResponseDto {
    @Schema(description = "결과")
    private ResultData result;

    @Data
    @NoArgsConstructor
    public static class ConfidenceValueObject {
        @Schema(description = "신뢰도")
        private double confidence;

        @Schema(description = "선택", example = "1(선택)")
        private double value;
    }

    @Data
    @NoArgsConstructor
    public static class SkinTypeObject {
        @Schema(description = "피부 타입")
        private int skinType;
    }

    @Data
    @NoArgsConstructor
    public static class ResultData {
        @Schema(description = "여드름")
        private ConfidenceValueObject acne;

        @Schema(description = "다크서클")
        private ConfidenceValueObject darkCircle;

        @Schema(description = "블랙헤드")
        private ConfidenceValueObject blackhead;

        @Schema(description = "왼쪽 볼 모공")
        private ConfidenceValueObject pores_left_cheek;

        @Schema(description = "이마 모공")
        private ConfidenceValueObject pores_forehead;

        @Schema(description = "턱 모공")
        private ConfidenceValueObject pores_jaw;

        @Schema(description = "오른쪽 볼 모공")
        private ConfidenceValueObject pores_right_cheek;

        @Schema(description = "피부 잡티")
        private ConfidenceValueObject skinSpot;

        @Schema(description = "점")
        private ConfidenceValueObject mole;

        @Schema(description = "피부 타입")
        private SkinTypeObject skinType;

        @Schema(description = "이마 주름")
        private ConfidenceValueObject forehead_wrinkle;

        @Schema(description = "눈가 주름")
        private ConfidenceValueObject crows_feet;

        @Schema(description = "눈밑 잔주름")
        private ConfidenceValueObject eye_finelines;

        @Schema(description = "미간 주름")
        private ConfidenceValueObject glabella_wrinkle;

        @Schema(description = "팔자 주름")
        private ConfidenceValueObject nasolabial_fold;
    }
}