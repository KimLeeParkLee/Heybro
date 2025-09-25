package com.heybro.heybro.coupon.repository;

import com.heybro.heybro.coupon.domain.CouponPurchase;
import com.heybro.heybro.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponPurchaseRepository extends JpaRepository<CouponPurchase, Long> {
    List<CouponPurchase> findAllByUserOrderByPurchaseDateDesc(User user);

    @Query("select cp from CouponPurchase cp join fetch cp.coupon where cp.user = :user order by cp.purchaseDate desc")
    List<CouponPurchase> findAllByUserWithCoupon(@Param("user") User user);
}
