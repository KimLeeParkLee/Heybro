package com.heybro.heybro.coupon.service;

import com.heybro.heybro.common.jwt.exception.ResourceNotFoundException;
import com.heybro.heybro.coupon.domain.Coupon;
import com.heybro.heybro.coupon.domain.CouponPurchase;
import com.heybro.heybro.coupon.domain.CouponType;
import com.heybro.heybro.coupon.dto.response.CouponListResponseDto;
import com.heybro.heybro.coupon.dto.response.CouponPurchaseResponseDto;
import com.heybro.heybro.coupon.dto.response.CouponResponseDto;
import com.heybro.heybro.coupon.repository.CouponPurchaseRepository;
import com.heybro.heybro.coupon.repository.CouponRepository;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final CouponPurchaseRepository couponPurchaseRepository;
    private final UserRepository userRepository;

    @Override
    public CouponListResponseDto getCoupons() {
        List<CouponResponseDto> amounts = new ArrayList<>();
        List<CouponResponseDto> products = new ArrayList<>();

        List<Coupon> coupons = couponRepository.findAllByOrderByIdAsc();

        // 쿠폰을 타입에 맞게 분류 : 금액권/상품권
        for (Coupon coupon : coupons) {
            CouponResponseDto dto = CouponResponseDto.builder()
                    .couponId(coupon.getId())
                    .name(coupon.getName())
                    .couponType(coupon.getCouponType())
                    .detailImage(coupon.getDetailImage())
                    .build();

            if (coupon.getCouponType() == CouponType.AMOUNT) amounts.add(dto);
            else if (coupon.getCouponType() == CouponType.PRODUCT) products.add(dto);
        }

        return CouponListResponseDto.builder()
                .amounts(amounts)
                .products(products)
                .build();
    }

    @Override
    public CouponResponseDto getCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                () -> new ResourceNotFoundException("해당 아이디를 가진 쿠폰을 찾을 수 없습니다: " + couponId)
        );

        return CouponResponseDto.builder()
                .couponId(coupon.getId())
                .name(coupon.getName())
                .couponType(coupon.getCouponType())
                .detailImage(coupon.getDetailImage())
                .build();
    }

    @Override
    public void purchaseCoupon(Long couponId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                () -> new ResourceNotFoundException("해당 아이디를 가진 쿠폰을 찾을 수 없습니다: " + couponId)
        );

        couponPurchaseRepository.save(CouponPurchase.builder()
                .purchaseDate(LocalDate.now())
                .coupon(coupon)
                .user(user)
                .build());
    }

    @Override
    public CouponPurchaseResponseDto findPurchasesByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        List<CouponPurchase> coupons = couponPurchaseRepository.findAllByUserOrderByPurchaseDateDesc(user);
        List<CouponResponseDto> usableCoupons = new ArrayList<>();
        List<CouponResponseDto> usedCoupons = new ArrayList<>();

        for (CouponPurchase couponPurchase : coupons) {
            if (!couponPurchase.isUsed()) {
                usableCoupons.add(CouponResponseDto.builder()
                        .couponId(couponPurchase.getCoupon().getId())
                        .name(couponPurchase.getCoupon().getName())
                        .couponType(couponPurchase.getCoupon().getCouponType())
                        .detailImage(couponPurchase.getCoupon().getDetailImage())
                        .build());
            } else {
                usedCoupons.add(CouponResponseDto.builder()
                        .couponId(couponPurchase.getCoupon().getId())
                        .name(couponPurchase.getCoupon().getName())
                        .couponType(couponPurchase.getCoupon().getCouponType())
                        .detailImage(couponPurchase.getCoupon().getDetailImage())
                        .build());
            }
        }

        return CouponPurchaseResponseDto.builder().usableCoupons(usableCoupons).usedCoupons(usedCoupons).build();
    }
}
