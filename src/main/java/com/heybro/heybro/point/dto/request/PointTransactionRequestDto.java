package com.heybro.heybro.point.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
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
    @Schema(description = "포인트 (1 이상)")
    @Positive(message = "포인트는 1 이상이어야 합니다.")
    private int point;
}
