package com.heybro.heybro.point.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "포인트 거래 요청 DTO")
public class PointTransactionRequestDto {
    @Schema(description = "포인트")
    private int point;
}
