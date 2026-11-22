package com.heybro.heybro.coupon.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "쿠폰 목록 응답 DTO")
public class CouponGiftCodeResponseDto {
    @Schema(description = "쿠폰 목록 응답 DTO")
    private String giftCode;
}
