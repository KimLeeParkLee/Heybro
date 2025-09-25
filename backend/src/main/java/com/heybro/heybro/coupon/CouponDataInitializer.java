package com.heybro.heybro.coupon;

import com.heybro.heybro.coupon.domain.Coupon;
import com.heybro.heybro.coupon.domain.CouponType;
import com.heybro.heybro.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponDataInitializer implements CommandLineRunner {
    private final CouponRepository couponRepository;

    @Override
    public void run(String... args) throws Exception {
        // 애플리케이션 시작 시점에 코드 실행
        if (couponRepository.count() > 0) {
            return;
        }

        // 금액권 저장하기
        couponRepository.save(Coupon.builder()
                .name("기프트카드 5천원권")
                .couponType(CouponType.AMOUNT)
                .price(5000)
                .detailImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/%EB%8B%A4%EC%9A%B4%EB%A1%9C%EB%93%9C.webp")
                .build());

        couponRepository.save(Coupon.builder()
                .name("기프트카드 1만원권")
                .couponType(CouponType.AMOUNT)
                .price(10000)
                .detailImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/100000.webp")
                .build());

        couponRepository.save(Coupon.builder()
                .name("기프트카드 2만원권")
                .couponType(CouponType.AMOUNT)
                .price(20000)
                .detailImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/20000.webp")
                .build());

        couponRepository.save(Coupon.builder()
                .name("기프트카드 3만원권")
                .couponType(CouponType.AMOUNT)
                .price(30000)
                .detailImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/30000.webp")
                .build());

        couponRepository.save(Coupon.builder()
                .name("기프트카드 5만원권")
                .couponType(CouponType.AMOUNT)
                .price(50000)
                .detailImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/50000.webp")
                .build());

        couponRepository.save(Coupon.builder()
                .name("기프트카드 10만원권")
                .couponType(CouponType.AMOUNT)
                .price(100000)
                .detailImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/10000.webp")
                .build());

        // 상품권 저장하기
        /*couponRepository.save(Coupon.builder()
                .name("올인원 퍼퓸 파워바")
                .couponType(CouponType.PRODUCT)
                .price(13000)
                .detailImage("https://drive.google.com/file/d/1Jw8ZHzWKHsXD1E7so-bCXt3vkgRj0Hwm/view?usp=sharing")
                .build());

        couponRepository.save(Coupon.builder()
                .name("올인원 쿨링바")
                .couponType(CouponType.PRODUCT)
                .price(13000)
                .detailImage("https://drive.google.com/file/d/19zPPCwbkp4aYQwWzN-HKCjsP8MMsAE1E/view?usp=sharing")
                .build());

        couponRepository.save(Coupon.builder()
                .name("워터맥스 선젤")
                .couponType(CouponType.PRODUCT)
                .price(21000)
                .detailImage("https://drive.google.com/file/d/1G2apskepkEfRM95dVHKDulkrPv_F-JBN/view?usp=sharing")
                .build());

        couponRepository.save(Coupon.builder()
                .name("카페인 트리플 블랙 샴푸")
                .couponType(CouponType.PRODUCT)
                .price(23000)
                .detailImage("https://drive.google.com/file/d/1ZUh6FYT6DpPL662Q1NeedR8xB6RZVWHw/view?usp=sharing")
                .build());

        couponRepository.save(Coupon.builder()
                .name("맨즈 브라운 올인원 로션")
                .couponType(CouponType.PRODUCT)
                .price(28000)
                .detailImage("https://drive.google.com/file/d/1yDbnmT622_k9uezmXwuy45fmB18AUBhd/view?usp=sharing")
                .build());*/
    }
}
