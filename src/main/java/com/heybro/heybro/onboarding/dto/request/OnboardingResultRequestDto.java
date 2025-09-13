package com.heybro.heybro.onboarding.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "온보딩 결과 요청 DTO")
public class OnboardingResultRequestDto {
    @Builder.Default
    @Schema(description = "답변 리스트")
    private List<AnswerDto> answers = new ArrayList<>();

    @Schema(description = "기상 시간")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime wakeupTime;

    @Schema(description = "취침 시간")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime bedtime;

    @Getter
    @NoArgsConstructor
    public static class AnswerDto {
        @Schema(description = "온보딩 질문 식별키")
        private Long onboardingQuestionId;

        @Schema(description = "온보딩 선택지 식별키")
        private Long onboardingOptionId;
    }
}
