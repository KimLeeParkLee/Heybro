package com.heybro.heybro.coupon.service;

import com.heybro.heybro.coupon.dto.response.CouponListResponseDto;
import com.heybro.heybro.coupon.dto.response.CouponResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface CouponService {
    CouponListResponseDto getCoupons();

    CouponResponseDto getCoupon(Long couponId);

    void purchaseCoupon(Long couponId, String email);
}
