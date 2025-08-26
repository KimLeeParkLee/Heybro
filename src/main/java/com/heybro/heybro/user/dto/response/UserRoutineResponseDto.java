package com.heybro.heybro.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.heybro.heybro.routine.domain.RoutineElement;
import com.heybro.heybro.routine.domain.RoutineTip;
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
@Schema(description = "회원 루틴 일정 응답 DTO")
public class UserRoutineResponseDto {
    @Schema(description = "루틴 리스트")
    List<RoutineResponseDto> routines = new ArrayList<>();

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "루틴 응답 DTO")
    public static class RoutineResponseDto {
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

        @Schema(description = "루틴 요소 리스트")
        private List<RoutineElementResponseDto> elements;

        @Schema(description = "루틴 팁 리스트")
        private List<RoutineTipResponseDto> tips;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "루틴 요소 응답 DTO")
    public static class RoutineElementResponseDto {
        @Schema(description = "루틴 요소 이름")
        private String routineElementName;

        @Schema(description = "단계")
        private int step;

        @Schema(description = "내용")
        private String routineElementContent;

        @Schema(description = "상세 이미지")
        private String detailImage;

        public static RoutineElementResponseDto from(RoutineElement element) {
            return RoutineElementResponseDto.builder()
                    .routineElementName(element.getName())
                    .step(element.getStep())
                    .routineElementContent(element.getContent())
                    .detailImage(element.getDetailImage())
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "루틴 팁 응답 DTO")
    public static class RoutineTipResponseDto {
        @Schema(description = "루틴 팁 내용")
        private String tipContent;

        @Schema(description = "루틴 팁 순서")
        private int order;

        public static RoutineTipResponseDto from(RoutineTip tip) {
            return RoutineTipResponseDto.builder()
                    .tipContent(tip.getContent())
                    .build();
        }
    }
}

