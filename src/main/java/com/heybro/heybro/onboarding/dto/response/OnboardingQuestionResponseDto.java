package com.heybro.heybro.onboarding.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "온보딩 질문 조회 응답 DTO")
public class OnboardingQuestionResponseDto {
    @Schema(description = "온보딩 질문 식별키")
    private Long onboardingQuestionId;

    @Schema(description = "질문 내용")
    private String questionContent;

    @Schema(description = "질문 순서")
    private int displayOrder;

    @Schema(description = "선택지 리스트")
    private List<OnboardingOptionsResponseDto> options = new ArrayList<>();
}
