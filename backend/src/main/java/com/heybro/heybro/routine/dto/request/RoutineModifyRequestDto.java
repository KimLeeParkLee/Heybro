package com.heybro.heybro.routine.dto.request;

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
@Schema(description = "루틴 수정 요청 DTO")
public class RoutineModifyRequestDto {
    @Schema(description = "루틴 정보 목록")
    @Builder.Default
    private List<RoutineInfoRequestDto> routineInfos = new ArrayList<>();
}
