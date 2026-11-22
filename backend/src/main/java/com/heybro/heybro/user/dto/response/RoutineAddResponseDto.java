package com.heybro.heybro.user.dto.response;

import com.heybro.heybro.routine.domain.Routine;
import com.heybro.heybro.routine.domain.TimeOfDay;
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
@Schema(description = "추가할 루틴 목록 응답 DTO")
public class RoutineAddResponseDto {
    @Builder.Default
    List<AvailableRoutineDto> routines = new ArrayList<>();

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "추가할 루틴")
    public static class AvailableRoutineDto {
        @Schema(description = "루틴 식별키")
        private Long routineId;

        @Schema(description = "루틴 이름")
        private String routineName;

        @Schema(description = "아이콘 url")
        private String iconImage;

        @Schema(description = "루틴 시간대 (MORNING, LUNCH, EVENING)")
        private TimeOfDay timeOfDay;

        public static AvailableRoutineDto from(Routine routine) {
            return AvailableRoutineDto.builder()
                    .routineId(routine.getId())
                    .routineName(routine.getName())
                    .iconImage(routine.getIconImage())
                    .timeOfDay(routine.getTimeOfDay())
                    .build();
        }
    }
}