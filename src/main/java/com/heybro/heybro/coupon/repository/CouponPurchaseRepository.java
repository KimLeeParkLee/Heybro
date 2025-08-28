package com.heybro.heybro.coupon.repository;

import com.heybro.heybro.coupon.domain.CouponPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponPurchaseRepository extends JpaRepository<CouponPurchase, Long> {
}
