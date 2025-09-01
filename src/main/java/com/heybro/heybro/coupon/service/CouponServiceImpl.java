package com.heybro.heybro.coupon.service;

import com.heybro.heybro.common.jwt.exception.ResourceNotFoundException;
import com.heybro.heybro.coupon.domain.Coupon;
import com.heybro.heybro.coupon.domain.CouponPurchase;
import com.heybro.heybro.coupon.domain.CouponType;
import com.heybro.heybro.coupon.domain.GiftStatus;
import com.heybro.heybro.coupon.dto.response.CouponGiftCodeResponseDto;
import com.heybro.heybro.coupon.dto.response.CouponListResponseDto;
import com.heybro.heybro.coupon.dto.response.CouponPurchaseResponseDto;
import com.heybro.heybro.coupon.dto.response.CouponResponseDto;
import com.heybro.heybro.coupon.repository.CouponPurchaseRepository;
import com.heybro.heybro.coupon.repository.CouponRepository;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final CouponPurchaseRepository couponPurchaseRepository;
    private final UserRepository userRepository;

    private final String COUPON_CODE_PREFIX = "BR";

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
                .giftCode(generateCouponCode())
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

    @Override
    @Transactional
    public CouponGiftCodeResponseDto giftCoupon(Long couponId, String email) {
        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        String giftCode = generateCouponCode();

        // 2. CouponPurchase 객체 생성 (선물용)
        CouponPurchase gift = CouponPurchase.builder()
                .sender(sender)
                .user(null) // 받는 사람은 아직 없음
                .coupon(coupon)
                .purchaseDate(LocalDate.now())
                .giftCode(giftCode)
                .giftStatus(GiftStatus.PENDING)
                .isUsed(false)
                .build();

        // 3. DB에 저장
        couponPurchaseRepository.save(gift);

        // 4. 생성된 고유 코드를 Controller로 반환
        return CouponGiftCodeResponseDto.builder().giftCode(giftCode).build();
    }

    private String generateCouponCode() {
        Random random = new Random();
        // 0부터 999999 사이의 숫자를 랜덤으로 생성
        int number = random.nextInt(1000000);
        // 6자리로 포맷팅 (앞자리가 비면 0으로 채움)
        return String.format("%s%06d", COUPON_CODE_PREFIX, number);
    }
}
