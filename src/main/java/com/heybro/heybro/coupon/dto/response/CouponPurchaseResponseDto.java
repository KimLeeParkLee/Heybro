package com.heybro.heybro.coupon.dto.response;

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
@Schema(description = "쿠폰 구매 내역 응답 DTO")
public class CouponPurchaseResponseDto {
    @Schema(description = "사용 가능한 쿠폰 목록")
    @Builder.Default
    List<CouponResponseDto> usableCoupons = new ArrayList<>();

    @Schema(description = "사용 완료한 쿠폰 목록")
    @Builder.Default
    List<CouponResponseDto> usedCoupons = new ArrayList<>();
}
