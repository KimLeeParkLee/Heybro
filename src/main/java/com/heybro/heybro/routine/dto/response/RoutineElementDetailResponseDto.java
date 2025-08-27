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
@Schema(description = "루틴 요소 상세 조회 응답 DTO")
public class RoutineElementDetailResponseDto {
    @Schema(description = "루틴 요소 리스트")
    List<RoutineElementResponseDto> elements = new ArrayList<>();

    @Schema(description = "루틴 팁 리스트")
    List<RoutineTipResponseDto> tips = new ArrayList<>();
}
