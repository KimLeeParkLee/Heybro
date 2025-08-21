package com.heybro.heybro.onboarding.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "온보딩 선택지 조회 응답 DTO")
public class OnboardingOptionsResponse {
    @Schema(description = "온보딩 선택지 식별키")
    private Long onboardingOptionId;

    @Schema(description = "선택지 내용")
    private String optionContent;

    @Schema(description = "선택지 순서")
    private int displayOrder;
}
