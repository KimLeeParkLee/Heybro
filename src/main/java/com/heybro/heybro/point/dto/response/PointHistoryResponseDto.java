package com.heybro.heybro.point.dto.response;

import com.heybro.heybro.point.domain.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "포인트 내역 응답 DTO")
public class PointHistoryResponseDto {
    @Schema(description = "금액")
    private int amount;

    @Enumerated(EnumType.STRING)
    @Schema(description = "변동 유형")
    private TransactionType transactionType; // 변동 유형

    @Schema(description = "변동 날짜")
    private LocalDateTime transactionDate; // 변동 날짜

    @Schema(description = "변동 사유")
    private String description; // 변동 사유
}
