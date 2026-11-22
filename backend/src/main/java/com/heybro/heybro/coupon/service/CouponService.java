package com.heybro.heybro.coupon.service;

import com.heybro.heybro.coupon.dto.response.CouponGiftCodeResponseDto;
import com.heybro.heybro.coupon.dto.response.CouponListResponseDto;
import com.heybro.heybro.coupon.dto.response.CouponPurchaseResponseDto;
import com.heybro.heybro.coupon.dto.response.CouponResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface CouponService {
    CouponListResponseDto getCoupons();

    CouponResponseDto getCoupon(Long couponId);

    void purchaseCoupon(Long couponId, String email);

    CouponPurchaseResponseDto findPurchasesByUser(String email);

    CouponGiftCodeResponseDto giftCoupon(Long couponId, String email);
}