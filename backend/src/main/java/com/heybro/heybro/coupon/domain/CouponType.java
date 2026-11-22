package com.heybro.heybro.coupon.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CouponType {
    AMOUNT("금액권"),
    PRODUCT("상품권");

    private final String description;
}
