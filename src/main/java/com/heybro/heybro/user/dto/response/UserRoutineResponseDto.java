package com.heybro.heybro.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.heybro.heybro.routine.domain.TimeOfDay;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 루틴 응답 DTO")
public class UserRoutineResponseDto {
    @Schema(description = "루틴 리스트")
    List<RoutineResponseDto> routines = new ArrayList<>();

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "루틴 응답 DTO")
    public static class RoutineResponseDto {
        @Schema(description = "루틴 식별키")
        private Long routineId;

        @Schema(description = "예정 시간")
        private LocalTime scheduleTime;

        @Schema(description = "루틴 이름")
        private String routineName;

        @Schema(description = "아이콘 url")
        private String iconImage;

        @JsonProperty("is_completed")
        @Schema(description = "완료 여부")
        private boolean completed;

        @Schema(description = "루틴 시간대 (MORNING, LUNCH, EVENING)")
        private TimeOfDay timeOfDay;
    }
}

