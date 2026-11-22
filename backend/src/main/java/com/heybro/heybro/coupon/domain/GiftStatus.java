package com.heybro.heybro.coupon.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GiftStatus {
    PENDING,  // 선물이 발송되었지만 아직 수신자가 등록하지 않은 상태
    ACCEPTED, // 수신자가 선물을 등록하여 자신의 쿠폰함에 넣은 상태
    EXPIRED   // 수신자가 등록하기 전에 유효기간이 만료된 상태
}