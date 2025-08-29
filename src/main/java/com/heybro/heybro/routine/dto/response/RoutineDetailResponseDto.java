package com.heybro.heybro.routine.dto.response;

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
@Schema(description = "루틴 상세 조회 응답 DTO")
public class RoutineDetailResponseDto {
    @Schema(description = "루틴 요소 리스트")
    @Builder.Default
    List<RoutineElementResponseDto> elements = new ArrayList<>();

    @Schema(description = "루틴 팁 리스트")
    @Builder.Default
    List<RoutineTipResponseDto> tips = new ArrayList<>();
}
