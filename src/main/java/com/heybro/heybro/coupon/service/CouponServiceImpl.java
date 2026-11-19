package com.heybro.heybro.coupon.service;

import com.heybro.heybro.common.date.service.DateService;
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
import com.heybro.heybro.point.domain.PointHistory;
import com.heybro.heybro.point.domain.TransactionType;
import com.heybro.heybro.point.repository.PointHistoryRepository;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final CouponPurchaseRepository couponPurchaseRepository;
    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;

    private final DateService dateService; // DateService 주입

    private static final String COUPON_CODE_PREFIX = "BR";
    private static final String COUPON_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 혼동되는 0/O, 1/I 제외
    private static final int COUPON_CODE_LENGTH = 10;
    private static final int COUPON_CODE_MAX_RETRY = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public CouponListResponseDto getCoupons() {
        List<CouponResponseDto> amounts = new ArrayList<>();
        List<CouponResponseDto> products = new ArrayList<>();

        List<Coupon> coupons = couponRepository.findAllByOrderByIdAsc();

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
    @Transactional
    public void purchaseCoupon(Long couponId, String email) {
        // 잔액 검증·차감·내역·구매 저장을 한 트랜잭션에서 처리. 회원 행은 비관적 락으로 잠가 동시 구매 시 잔액 이상 구매를 방지
        User user = userRepository.findByEmailForUpdate(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        Coupon coupon = couponRepository.findById(couponId).orElseThrow(
                () -> new ResourceNotFoundException("해당 아이디를 가진 쿠폰을 찾을 수 없습니다: " + couponId)
        );

        deductPoints(user, coupon, "쿠폰 구매: " + coupon.getName());

        couponPurchaseRepository.save(CouponPurchase.builder()
                .giftCode(generateUniqueCouponCode())
                .purchaseDate(dateService.getToday()) // 수정: DateService 사용
                .isUsed(false)
                .coupon(coupon)
                .user(user)
                .build());
    }

    // 쿠폰 가격만큼 포인트를 차감하고 변동 내역을 남긴다. 가격은 클라이언트가 아닌 서버의 Coupon.price 로 결정
    private void deductPoints(User user, Coupon coupon, String description) {
        int price = coupon.getPrice();
        if (price < 0) {
            throw new IllegalStateException("쿠폰 가격이 올바르지 않습니다: " + price);
        }
        if (user.getBroPoint() < price) {
            throw new IllegalArgumentException("포인트가 부족합니다. 필요: " + price + ", 보유: " + user.getBroPoint());
        }
        user.usePoints(price);
        pointHistoryRepository.save(PointHistory.builder()
                .amount(price)
                .transactionDate(LocalDateTime.now())
                .transactionType(TransactionType.USE)
                .description(description)
                .user(user)
                .build());
    }

    @Override
    public CouponPurchaseResponseDto findPurchasesByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        List<CouponPurchase> coupons = couponPurchaseRepository.findAllByUserWithCoupon(user);
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
        // 선물도 구매와 같이 보내는 사람의 포인트를 락 안에서 차감
        User sender = userRepository.findByEmailForUpdate(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 아이디를 가진 쿠폰을 찾을 수 없습니다: " + couponId));

        deductPoints(sender, coupon, "쿠폰 선물: " + coupon.getName());

        String giftCode = generateUniqueCouponCode();

        CouponPurchase gift = CouponPurchase.builder()
                .sender(sender)
                .user(null)
                .coupon(coupon)
                .purchaseDate(dateService.getToday()) // 수정: DateService 사용
                .giftCode(giftCode)
                .giftStatus(GiftStatus.PENDING)
                .isUsed(false)
                .build();

        couponPurchaseRepository.save(gift);

        return CouponGiftCodeResponseDto.builder().giftCode(giftCode).build();
    }

    // 기존 6자리 숫자(100만 가지)는 약 1,200건부터 충돌 확률이 50%를 넘어 unique 제약 위반(500)이 났다.
    // SecureRandom 10자리(32^10)로 늘리고, 혹시 모를 충돌은 DB 조회로 확인해 재시도한다.
    private String generateUniqueCouponCode() {
        for (int attempt = 0; attempt < COUPON_CODE_MAX_RETRY; attempt++) {
            StringBuilder sb = new StringBuilder(COUPON_CODE_PREFIX);
            for (int i = 0; i < COUPON_CODE_LENGTH; i++) {
                sb.append(COUPON_CODE_CHARS.charAt(SECURE_RANDOM.nextInt(COUPON_CODE_CHARS.length())));
            }
            String code = sb.toString();
            if (!couponPurchaseRepository.existsByGiftCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("쿠폰 코드 생성에 실패했습니다. 잠시 후 다시 시도해 주세요.");
    }
}
