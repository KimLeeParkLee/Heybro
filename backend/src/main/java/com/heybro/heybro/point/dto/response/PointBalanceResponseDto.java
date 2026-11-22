package com.heybro.heybro.point.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "포인트 조회 응답 DTO")
public class PointBalanceResponseDto {
    @Schema(description = "포인트")
    private int point;
}

