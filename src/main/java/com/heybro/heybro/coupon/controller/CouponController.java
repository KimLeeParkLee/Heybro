package com.heybro.heybro.coupon.controller;

import com.heybro.heybro.coupon.dto.response.CouponListResponseDto;
import com.heybro.heybro.coupon.dto.response.CouponResponseDto;
import com.heybro.heybro.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
@Tag(name = "쿠폰", description = "쿠폰 API")
public class CouponController {
    private final CouponService couponService;

    @Operation(summary = "쿠폰 목록 조회")
    @GetMapping
    public CouponListResponseDto getCoupons() {
        return couponService.getCoupons();
    }

    @Operation(summary = "쿠폰 상세 조회")
    @GetMapping("/{coupon_id}")
    public CouponResponseDto getCoupon(@PathVariable Long coupon_id) {
        return couponService.getCoupon(coupon_id);
    }

    @Operation(summary = "쿠폰 구매")
    @PostMapping("/{coupon_id}/purchase")
    public void purchaseCoupon(@PathVariable Long coupon_id, @AuthenticationPrincipal UserDetails userDetails) {
        couponService.purchaseCoupon(coupon_id, userDetails.getUsername());
    }
}
