package com.heybro.heybro.coupon.domain;

import com.heybro.heybro.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CouponPurchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 쿠폰 구매 내역 식별키

    private boolean isUsed; // 사용 여부

    private LocalDate usedAt; // 사용 날짜

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon; // 쿠폰

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
