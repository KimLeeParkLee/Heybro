package com.heybro.heybro.coupon.service;

import com.heybro.heybro.coupon.dto.response.CouponListResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface CouponService {
    CouponListResponseDto getCoupons();
}
