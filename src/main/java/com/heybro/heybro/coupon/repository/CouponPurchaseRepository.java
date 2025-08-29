package com.heybro.heybro.coupon.repository;

import com.heybro.heybro.coupon.domain.CouponPurchase;
import com.heybro.heybro.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponPurchaseRepository extends JpaRepository<CouponPurchase, Long> {
    List<CouponPurchase> findAllByUserOrderByPurchaseDateDesc(User user);
}
