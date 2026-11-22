package com.heybro.heybro.coupon;

import com.heybro.heybro.coupon.domain.Coupon;
import com.heybro.heybro.coupon.domain.CouponType;
import com.heybro.heybro.coupon.repository.CouponPurchaseRepository;
import com.heybro.heybro.coupon.repository.CouponRepository;
import com.heybro.heybro.coupon.service.CouponService;
import com.heybro.heybro.point.repository.PointHistoryRepository;
import com.heybro.heybro.support.IntegrationTestSupport;
import com.heybro.heybro.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class CouponPurchaseConcurrencyTest extends IntegrationTestSupport {

    @Autowired CouponService couponService;
    @Autowired CouponRepository couponRepository;
    @Autowired CouponPurchaseRepository couponPurchaseRepository;
    @Autowired PointHistoryRepository pointHistoryRepository;

    @Test
    @DisplayName("10P 쿠폰을 잔액 1,000 으로 120건 동시 구매하면 100건만 성공하고 포인트·구매 내역이 일치한다")
    void purchaseCoupon_concurrently_deductsPointsExactly() throws Exception {
        User user = saveUser("coupon-concurrency@test.local", 1000);
        Coupon coupon = couponRepository.save(Coupon.builder()
                .name("테스트 쿠폰").couponType(CouponType.PRODUCT).price(10).detailImage("x").build());

        ConcurrentResult result = runConcurrently(120, () -> couponService.purchaseCoupon(coupon.getId(), user.getEmail()));

        User after = userRepository.findById(user.getId()).orElseThrow();
        assertThat(result.success()).isEqualTo(100);
        assertThat(after.getBroPoint()).isZero();
        assertThat(couponPurchaseRepository.findAllByUserWithCoupon(after)).hasSize(100);
        assertThat(pointHistoryRepository.findByUserOrderByTransactionDateDesc(after)).hasSize(100);
        // 쿠폰 코드는 전부 달라야 한다
        assertThat(couponPurchaseRepository.findAllByUserWithCoupon(after).stream().map(p -> p.getGiftCode()).distinct()).hasSize(100);
    }
}
