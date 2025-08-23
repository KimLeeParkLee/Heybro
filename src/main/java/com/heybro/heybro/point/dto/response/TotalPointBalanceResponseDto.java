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
@Schema(description = "누적 포인트 조회 응답 DTO")
public class TotalPointBalanceResponseDto {
    @Schema(description = "누적 포인트")
    private int totalPoint;
}
