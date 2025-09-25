package com.heybro.heybro.routine.dto.response;

import com.heybro.heybro.routine.domain.RoutineTip;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "루틴 팁 응답 DTO")
public class RoutineTipResponseDto {
    @Schema(description = "루틴 팁 식별키")
    private Long routineTipId;

    @Schema(description = "루틴 팁 내용")
    private String tipContent;

    @Schema(description = "루틴 팁 순서")
    private int order;

    public static RoutineTipResponseDto from(RoutineTip tip) {
        return RoutineTipResponseDto.builder()
                .routineTipId(tip.getId())
                .tipContent(tip.getContent())
                .build();
    }
}
