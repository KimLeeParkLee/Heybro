package com.heybro.heybro.coupon.dto.response;

import com.heybro.heybro.coupon.domain.CouponType;
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
@Schema(description = "쿠폰 응답 DTO")
public class CouponResponseDto {
    @Schema(description = "쿠폰 식별키")
    private Long couponId;

    @Schema(description = "쿠폰 이름")
    private String name;

    @Schema(description = "쿠폰 이름")
    private CouponType couponType;

    @Schema(description = "상세 사진")
    private String detailImage;
}
