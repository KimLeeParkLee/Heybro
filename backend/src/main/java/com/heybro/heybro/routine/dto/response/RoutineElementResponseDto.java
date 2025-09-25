package com.heybro.heybro.routine.dto.response;

import com.heybro.heybro.routine.domain.RoutineElement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "루틴 요소 응답 DTO")
public class RoutineElementResponseDto {
    @Schema(description = "루틴 요소 식별키")
    private Long routineElementId;

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
                .routineElementId(element.getId())
                .routineElementName(element.getName())
                .step(element.getStep())
                .routineElementContent(element.getContent())
                .detailImage(element.getDetailImage())
                .build();
    }
}