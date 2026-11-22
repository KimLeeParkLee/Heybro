package com.heybro.heybro.routine.dto.response;

import com.heybro.heybro.routine.domain.PeriodType;
import com.heybro.heybro.routine.domain.ViewType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "루틴 달성률 단일 응답 DTO")
public class AchievementSummaryResponseDto {
    @Schema(description = "조회 유형")
    ViewType view;

    @Schema(description = "기간 유형")
    PeriodType period;

    @Schema(description = "조회 날짜")
    LocalDate date;

    @Schema(description = "루틴 달성률")
    int achievementRate;
}
