package com.heybro.heybro.coupon.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 쿠폰 식별키

    private String name; // 쿠폰 이름

    private CouponType couponType; // 쿠폰 타입

    private int price; // 가격

    private String code; // 번호

    private String detailImage; // 상세 사진
}
