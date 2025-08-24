package com.heybro.heybro.onboarding.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "온보딩 결과 요청 DTO")
public class OnboardingResultRequestDto {
    @Schema(description = "답변 리스트")
    private List<AnswerDto> answers;

    @Schema(description = "기상 시간")
    private LocalTime wakeupTime;

    @Schema(description = "취침 시간")
    private LocalTime bedtime;

    @Getter
    @NoArgsConstructor
    public static class AnswerDto {
        @JsonProperty("onboarding_question_id")
        @Schema(description = "온보딩 질문 식별키")
        private Long onboardingQuestionId;

        @JsonProperty("onboarding_option_id")
        @Schema(description = "온보딩 선택지 식별키")
        private Long onboardingOptionId;
    }
}
