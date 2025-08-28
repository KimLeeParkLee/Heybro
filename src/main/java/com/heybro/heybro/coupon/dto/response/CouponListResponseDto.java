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
@Schema(description = "쿠폰 목록 응답 DTO")
public class CouponListResponseDto {
    @Schema(description = "금액권 쿠폰 목록")
    List<CouponResponseDto> amounts = new ArrayList<>();

    @Schema(description = "상품권 쿠폰 목록")
    List<CouponResponseDto> products = new ArrayList<>();
}
